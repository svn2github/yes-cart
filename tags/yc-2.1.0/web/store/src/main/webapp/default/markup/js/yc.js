var ctx = {
    url: document.URL,
    root: '/yes-shop', /* This is how it appears from WicketUtil.getHttpServletRequest().getContextPath(). */
    page: 'HomePage',
    showModalWindow: function(){}
};

$(document).ready(function() {

    var _categoryId = function() {
        var urlPart = document.URL.split("/");
        var hasCategoryId = false;
        for (partNum = 0; partNum < urlPart.length; partNum++) {
            if (hasCategoryId) {
                return  "/category/" + urlPart[partNum];
            }
            hasCategoryId = ('category' == urlPart[partNum]);
        }
        return "";
    }();


    // highlight tag links
    $('a.jsTag').each(function() {
        var _url = 'tag/' + $(this).attr('id');
        var _fullUrl = ctx.root + '/' + _url + (_categoryId.length > 0 ? _categoryId : '');
        $(this).attr('href', _fullUrl);
        if (ctx.url.indexOf(_url) != -1) {
            $(this).addClass('active-tag');
        }
    });

    // highlight page links
    $('a.jsPage').each(function() {
        var _url = $(this).attr('href');
        if (ctx.url.indexOf(_url) != -1) {
            $(this).addClass('active-page');
        }
    });

    // create modal message
    var createModalMsg = function(node) {
        var _inner = $('<div class="modal-message-inner"/>');
        _inner.append(node);
        var _outer = $('<div class="modal-message"/>');
        _outer.append(_inner);
        return _outer;
    };

    ctx.showModalWindow = function(node) {
        var _msgNode = createModalMsg(node.html());
        $('body').append(_msgNode);
        setTimeout(function () {
            _msgNode.fadeOut(1000, function(){
                _msgNode.remove();
            });
        }, 2500);
    };
});

