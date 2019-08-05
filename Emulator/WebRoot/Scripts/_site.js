// ng-app
var app = angular.module('portalApp', ["ui.bootstrap", "ngAnimate", "slotGame", "lobbyNav", "portalShared"]);

/* region 余额宝连结入口 */
// <tag yuebao-link is-login="@ViewBag.IsLogin.ToString().ToLower()"></tag>
// yuebao-remove僅需下在account-nav中
app.directive("siteYuebaoLink", ['$compile', 'FunctionSwitchService', function ($compile, FunctionSwitchService) {
    return {
        link: function (scope, element, attrs) {
            //是否有登入
            var isLogin = attrs.isLogin === 'true';
            //余额宝文字
            var yuebaoTitle = element[0].hasAttribute('yuebao-title') ? attrs.yuebaoTitle : '';
            //是否有需要删除
            var isYuebaoRemove = element[0].hasAttribute('yuebao-remove');
            //禁用页面
            var isPage = attrs.notShowPage ? attrs.notShowPage.replace(/\//g, '').replace(/,/g, '|') : false;
            //当前页面
            var currentPage = !!$('#home').length || location.pathname === '/' ? 'home' : location.pathname.replace(/\//g, '');
            //有连结
            var yuebaolink = '<a href="javascript:void(0)" data-popup-opener="/Yuebao" data-popup-opener-options="width = 1284, height = 893, resizable, scrollbars = yes" data-popup-opener-name="_yuebao">' + yuebaoTitle + '</a>';
            //无连结
            var yuebaoNoLink = '<a href="javascript:void(0)">' + yuebaoTitle + '</a>';

            //是否有禁用頁面
            if (isPage) {
                var regex = new RegExp(isPage, 'i');
                if (regex.test(currentPage)) {
                    element.remove();
                    return;
                }
            }

            FunctionSwitchService.getFunctionSwitch()
                .then(function (resp) {
                    //是否有打开
                    var isYuebaoUnabled = resp.YuebaoSwitch;
                    if (isLogin && isYuebaoUnabled) {
                        element.html($compile(yuebaolink)(scope));
                    } else {
                        return isYuebaoRemove ? element.remove() : element.html(yuebaoNoLink);
                    }
                });
        }
    };
}]);
/* endregion 余额宝连结入口 */
