/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.dao.impl;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;
import org.hibernate.search.util.impl.ClassLoaderHelper;
import org.hibernate.search.util.impl.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.util.ShopCodeContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class GenericDAOHibernateImpl<T, PK extends Serializable>
        implements GenericDAO<T, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDAOHibernateImpl.class);

    private final Class<T> persistentClass;
    private final EntityFactory entityFactory;
    private final EntityIndexingInterceptor entityIndexingInterceptor;
    protected SessionFactory sessionFactory;

    private TaskExecutor indexExecutor;


    /**
     * Set the Hibernate SessionFactory to be used by this DAO.
     * Will automatically create a HibernateTemplate for the given SessionFactory.
     */
    public final void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    /**
     * Executer that will perform indexing jobs asynchronously.
     *
     * @param indexExecutor index executor
     */
    public void setIndexExecutor(final TaskExecutor indexExecutor) {
        this.indexExecutor = indexExecutor;
    }

    /**
     * Default constructor.
     *
     * @param type          - entity type
     * @param entityFactory {@link EntityFactory} to create the entities
     */
    @SuppressWarnings("unchecked")
    public GenericDAOHibernateImpl(
            final Class<T> type,
            final EntityFactory entityFactory) {
        this.persistentClass = type;
        this.entityFactory = entityFactory;
        this.entityIndexingInterceptor = getInterceptor();
    }

    private EntityIndexingInterceptor getInterceptor() {
        final Indexed indexed = getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class);
        if (indexed != null) {
            final Class<? extends EntityIndexingInterceptor> interceptorClass = indexed.interceptor();
            if (interceptorClass != null) {
                return ClassLoaderHelper.instanceFromClass(
                        EntityIndexingInterceptor.class,
                        interceptorClass,
                        "IndexingActionInterceptor for " + getPersistentClass().getName()
                );
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    /**
     * {@inheritDoc}
     */
    public <I> I getEntityIdentifier(final Object entity) {
        if (entity == null) {
            // That's ok - it is null
            return null;
        } if (entity instanceof HibernateProxy && !Hibernate.isInitialized(entity)) {
            // Avoid Lazy select by getting identifier from session meta
            // If hibernate proxy is initialised then DO NOT use this approach as chances
            // are that this is detached entity from cache which is not associate with the
            // session and will result in exception
            return (I) sessionFactory.getCurrentSession().getIdentifier(entity);
        } else if (entity instanceof Identifiable) {
            // If it is not proxy or it is initialised then we can use identifiable
            return (I) Long.valueOf(((Identifiable) entity).getId());
        }
        throw new IllegalArgumentException("Cannot get PK from object: " + entity);
    }

    /**
     * {@inheritDoc}
     */
    public T findById(PK id) {
        return findById(id, false);
    }

    private static final LockOptions UPDATE = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findById(final PK id, final boolean lock) {
        T entity;
        if (lock) {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id, UPDATE);
        } else {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(final T exampleInstance, final String[] excludeProperty) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        List<T> rez = (List<T>) this.findByNamedQuery(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T findSingleByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        List<T> rez = (List<T>) this.findByNamedQueryCached(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQueryWithInit(final String namedQueryName,  final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        final Object obj = query.uniqueResult();
        if (obj instanceof Product) {
            Hibernate.initialize(((Product) obj).getAttributes());

        }
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        setQueryParameters(query, parameters);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        setQueryParameters(query, parameters);
        final List rez = query.list();
        int size = rez.size();
        switch (size) {
            case 0: {
                return null;
            }
            case 1: {
                return rez.get(0);
            }
            default: {
                ShopCodeContext.getLog(this).error("#findSingleByQuery has more than one result for " + hsqlQuery);
                return rez.get(0);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return new ResultsIteratorImpl<T>(query.scroll(ScrollMode.FORWARD_ONLY));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        LockOptions opts = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        opts.setTimeOut(timeout);
        query.setLockOptions(opts);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        query.setCacheable(true);
        query.setCacheMode(CacheMode.NORMAL);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsByNamedQueryWithList(final String namedQueryName, final List parameter) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        query.setParameterList("list", parameter);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findQueryObjectsByNamedQueryWithList(
            final String namedQueryName, final Collection<Object> listParameter,
            final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        query.setParameterList("list", listParameter);
        setQueryParameters(query, parameters);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findRangeByNamedQuery(final String namedQueryName,
                                         final int firtsResult,
                                         final int maxResults,
                                         final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        query.setFirstResult(firtsResult);
        query.setMaxResults(maxResults);
        setQueryParameters(query, parameters);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return findByCriteria();
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findAllIterator() {
        final Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        final ScrollableResults results = crit.scroll(ScrollMode.FORWARD_ONLY);
        return new ResultsIteratorImpl<T>(results);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T create(T entity) {
        sessionFactory.getCurrentSession().save(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T update(T entity) {
        sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Object entity) {
        if (entity != null) {
            sessionFactory.getCurrentSession().delete(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void evict(Object entity) {
        sessionFactory.getCurrentSession().evict(entity);

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        return findSingleByCriteria(null, criterion);
    }

    /**
     * Find entities by criteria.
     * @param criteriaTuner optional criteria tuner.
     * @param criterion given criteria
     * @return list of found entities.
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(CriteriaTuner criteriaTuner, Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        return crit.list();

    }

    /**
     * Find entities by criteria.
     * @param firstResult scroll to first result.
     * @param criterion given criteria
     * @return list of found entities.
     */
    @SuppressWarnings("unchecked")
    public T findUniqueByCriteria(final int firstResult, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return (T)  crit.setFirstResult(firstResult).setMaxResults(1).uniqueResult();

    }


    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        return (T) crit.uniqueResult();
    }


    private Class<T> getPersistentClass() {
        return persistentClass;
    }


    public int fullTextSearchReindex(PK primaryKey, boolean purgeOnly) {
        int result = 0;
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            sessionFactory.getCache().evictEntity(getPersistentClass(), primaryKey);

            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            fullTextSession.setFlushMode(FlushMode.MANUAL);
            fullTextSession.setCacheMode(CacheMode.IGNORE);
            fullTextSession.purge(getPersistentClass(), primaryKey);
            if (!purgeOnly) {
                T entity = findById(primaryKey);
                if(entity != null) {
                    final T unproxied = (T) HibernateHelper.unproxy(entity);

                    if (entityIndexingInterceptor != null) {
                        if (IndexingOverride.APPLY_DEFAULT == entityIndexingInterceptor.onAdd(unproxied)) {
                            fullTextSession.index(unproxied);
                        }
                    } else {
                        fullTextSession.index(unproxied);
                    }
                }


            }
            result++;
            fullTextSession.flushToIndexes(); //apply changes to indexes
            fullTextSession.clear(); //clear since the queue is processed

        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public int fullTextSearchReindex(final PK primaryKey) {
        return  fullTextSearchReindex(primaryKey, false);
    }

    private final int IDLE = -3;
    private final int COMPLETED = -1;
    private final int LASTUPDATE = -2;
    private final int RUNNING = 0;

    private final AtomicInteger asyncRunningState = new AtomicInteger(IDLE);
    private final AtomicInteger currentIndexingCount = new AtomicInteger(0);

    /**
     * {@inheritDoc}
     */
    public int fullTextSearchReindex(final boolean async) {

        final int[] count = new int[] { 0 };
        final boolean runAsync = async && this.indexExecutor != null;
        if (!runAsync) {
            createIndexingRunnable(false, count).run(); // sync
            return count[0];
        }

        if (asyncRunningState.get() == RUNNING) {
            // indexing already in progress
            return currentIndexingCount.get();
        }

        if (asyncRunningState.compareAndSet(COMPLETED, LASTUPDATE)) {
            // last update of the count from the indexing with final count
            return currentIndexingCount.get();
        }

        if (asyncRunningState.compareAndSet(LASTUPDATE, IDLE)) {
            // thread had finished, so need to set this to idle
            // if this is not set then this may turn into endless recursion
            // from pinging YUM
            currentIndexingCount.set(0);
            return -1; // must be negative as the signal to job to stop
        }

        if (!asyncRunningState.compareAndSet(IDLE, RUNNING)) {
            // indexing started just now on another thread
            return currentIndexingCount.get();
        }

        this.indexExecutor.execute(createIndexingRunnable(true, new int[1])); // async

        return currentIndexingCount.get();
    }

    private Runnable createIndexingRunnable(final boolean async, final int[] count) {
        final int BATCH_SIZE = 20;
        return new Runnable() {
            @Override
            public void run() {
                int index = 0;
                try {

                    if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
                        FullTextSession fullTextSession = Search.getFullTextSession(async ? sessionFactory.openSession() : sessionFactory.getCurrentSession());
                        fullTextSession.setFlushMode(FlushMode.MANUAL);
                        fullTextSession.setCacheMode(CacheMode.IGNORE);
                        fullTextSession.purgeAll(getPersistentClass());
                        fullTextSession.getSearchFactory().optimize(getPersistentClass());
                        ScrollableResults results = fullTextSession.createCriteria(persistentClass)
                                .setFetchSize(BATCH_SIZE)
                                .scroll(ScrollMode.FORWARD_ONLY);

                        final Logger log = ShopCodeContext.getLog(this);
                        while (results.next()) {
                            index++;
                            T entity = (T) HibernateHelper.unproxy(results.get(0));

                            if (entityIndexingInterceptor != null) {
                                if (IndexingOverride.APPLY_DEFAULT == entityIndexingInterceptor.onAdd(entity)) {
                                    fullTextSession.index(entity);
                                }
                            } else {
                                fullTextSession.index(entity);
                            }
                            if (index % BATCH_SIZE == 0) {
                                fullTextSession.flushToIndexes(); //apply changes to indexes
                                fullTextSession.clear(); //clear since the queue is processed
                                if (log.isInfoEnabled()) {
                                    log.info("Indexed " + index + " items of " + persistentClass + " class");
                                }
                            }
                            if (async) {
                                currentIndexingCount.compareAndSet(index - 1, index);
                            }
                        }
                        fullTextSession.flushToIndexes(); //apply changes to indexes
                        fullTextSession.clear(); //clear since the queue is processed
                        if (log.isInfoEnabled()) {
                            log.info("Indexed " + index + " items of " + persistentClass + " class");
                        }
                    }
                } catch (Exception exp) {
                    LOG.error("Error during indexing", exp);
                } finally {
                    count[0] = index;
                    if (async) {
                        asyncRunningState.set(COMPLETED);
                        try {
                            sessionFactory.getCurrentSession().close();
                        } catch (Exception exp) { }
                    }
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query) {
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            Query fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            List<T> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firtsResult,
                                  final int maxResults) {
        return fullTextSearch(query, firtsResult, maxResults, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firtsResult,
                                  final int maxResults,
                                  final String sortFieldName) {
        return fullTextSearch(query, firtsResult, maxResults, sortFieldName, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> fullTextSearch(final org.apache.lucene.search.Query query,
                                         final int firstResult,
                                         final int maxResults,
                                         final String sortFieldName,
                                         final boolean reverse,
                                         final String ... fields) {
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            final FullTextQuery fullTextQuery = createFullTextQuery(query, firstResult, maxResults, sortFieldName, reverse);
            fullTextQuery.setProjection(fields);
            final List<Object[]> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }

        }
        return Collections.EMPTY_LIST;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firtsResult,
                                  final int maxResults,
                                  final String sortFieldName,
                                  final boolean reverse) {
        if (null != getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class)) {
            final FullTextQuery fullTextQuery = createFullTextQuery(query, firtsResult, maxResults, sortFieldName, reverse);
            final List<T> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private FullTextQuery createFullTextQuery(final org.apache.lucene.search.Query query,
                                              final int firtsResult,
                                              final int maxResults,
                                              final String sortFieldName,
                                              final boolean reverse) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
        if (sortFieldName != null) {
            Sort sort = new Sort(
                    new SortField(sortFieldName, SortField.STRING, reverse));
            fullTextQuery.setSort(sort);
        }
        fullTextQuery.setFirstResult(firtsResult);
        fullTextQuery.setMaxResults(maxResults);
        return fullTextQuery;
    }


    /**
     * {@inheritDoc}
     */
    public int getResultCount(final org.apache.lucene.search.Query query) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        return fullTextSession.createFullTextQuery(query, getPersistentClass()).getResultSize();
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(nativeQuery);
        return sqlQuery.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public List executeNativeQuery(final String nativeQuery) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(nativeQuery);
        return sqlQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    public List executeHsqlQuery(final String hsql) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsql);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsql);
        setQueryParameters(query, parameters);
        return query.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(nativeQuery);
        setQueryParameters(sqlQuery, parameters);
        return sqlQuery.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        final Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.executeUpdate();
    }

    private void setQueryParameters(final Query query, final Object[] parameters) {
        if (parameters != null) {
            int idx = 1;
            for (Object param : parameters) {
                if (param instanceof Collection) {
                    query.setParameterList(String.valueOf(idx), (Collection) param);
                } else {
                    query.setParameter(String.valueOf(idx), param);
                }
                idx++;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flushClear() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        sessionFactory.getCurrentSession().clear();
    }

}
