/**
 * @author: Alec Fenichel
 * @webSite: https://fenichelar.com
 * @version: v1.0.0
 */

(function ($) {

    'use strict';

    $.extend($.fn.bootstrapTable.defaults, {
        autoRefresh: false,
        autoRefreshInterval: 60,
        autoRefreshSilent: true,
        autoRefreshStatus: true,
        autoRefreshFunction: null
    });

    $.extend($.fn.bootstrapTable.defaults.icons, {
        autoRefresh: 'fa-refresh fa-spin'
    });

    $.extend($.fn.bootstrapTable.locales, {
        formatAutoRefresh: function() {
            return '自动刷新';
        }
    });

    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales);

    var BootstrapTable = $.fn.bootstrapTable.Constructor;
    var _init = BootstrapTable.prototype.init;
    var _initToolbar = BootstrapTable.prototype.initToolbar;
    var sprintf = $.fn.bootstrapTable.utils.sprintf;

    BootstrapTable.prototype.init = function () {
        _init.apply(this, Array.prototype.slice.apply(arguments));

        if (this.options.autoRefresh && this.options.autoRefreshStatus) {
            var that = this;
            this.options.autoRefreshFunction = setInterval(function () {
                that.refresh({silent: that.options.autoRefreshSilent});
            }, this.options.autoRefreshInterval*1000);
        }
    };

    BootstrapTable.prototype.initToolbar = function() {
        _initToolbar.apply(this, Array.prototype.slice.apply(arguments));

        if (this.options.autoRefresh) {
            var $btnGroup = this.$toolbar.find('>.btn-group');
            var $btnAutoRefresh = $btnGroup.find('.auto-refresh');

            if (!$btnAutoRefresh.length) {
                $btnAutoRefresh = $([
                    sprintf('<button class="btn' +
                            sprintf(' btn-%s', this.options.buttonsClass) +
                            sprintf(' btn-%s', this.options.iconSize) +' auto-refresh %s" ', this.options.autoRefreshStatus ? 'enabled' : ''),
                    'type="button" ',
                    sprintf('title="%s">', this.options.formatAutoRefresh()),
                    sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.autoRefresh),
                    '</button>'
                ].join('')).appendTo($btnGroup);

                $btnAutoRefresh.on('click', $.proxy(this.toggleAutoRefresh, this));
            }
        }
    };

    BootstrapTable.prototype.toggleAutoRefresh = function() {
        if (this.options.autoRefresh) {
            if (this.options.autoRefreshStatus) {
                clearInterval(this.options.autoRefreshFunction);
                this.$toolbar.find('>.btn-group').find('.auto-refresh').html('<i class="fa fa-play"></i>');
            } else {
                var that = this;
                this.options.autoRefreshFunction = setInterval(function () {
                    that.refresh({silent: that.options.autoRefreshSilent});
                }, this.options.autoRefreshInterval*1000);
                this.$toolbar.find('>.btn-group').find('.auto-refresh').html('<i class="fa fa-refresh fa-spin"></i>');
            }
            this.options.autoRefreshStatus = !this.options.autoRefreshStatus;
        }
    };

})(jQuery);
