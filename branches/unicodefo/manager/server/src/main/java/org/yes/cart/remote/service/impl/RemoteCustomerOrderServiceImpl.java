package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCustomerOrderService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCustomerOrderServiceImpl
        extends AbstractRemoteService<CustomerOrderDTO>
        implements RemoteCustomerOrderService {

    /**
     * Construct remote service
     *
     * @param customerOrderDTOGenericDTOService
     *         dto serivese to use.
     */
    public RemoteCustomerOrderServiceImpl(final GenericDTOService<CustomerOrderDTO> customerOrderDTOGenericDTOService) {
        super(customerOrderDTOGenericDTOService);
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDTO> findCustomerOrdersByCriterias(
            final long customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String orderStatus,
            final Date fromDate,
            final Date tillDate,
            final String orderNum
    ) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCustomerOrderService) getGenericDTOService()).findCustomerOrdersByCriterias(
                customerId,
                firstName,
                lastName,
                email,
                orderStatus,
                fromDate,
                tillDate,
                orderNum
        );
    }


}
