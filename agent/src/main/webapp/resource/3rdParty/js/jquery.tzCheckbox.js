(function ($) {
    $.fn.tzCheckbox = function (options) {
        // Default On / Off labels:		
        options = $.extend({
            labels: ['ON', 'OFF'],
            selectCheckboxCallback: ''
        }, options);

        return this.each(function () {
            var originalCheckBox = $(this),
				labels = [];

            // Checking for the data-on / data-off HTML5 data attributes:
            if (originalCheckBox.data('on')) {
                labels[0] = originalCheckBox.data('on');
                labels[1] = originalCheckBox.data('off');
            }
            else labels = options.labels;
            // Creating the new checkbox markup:
            var checkBox = $('<span>', {
                html: '<span class="tzCBContent">' + labels[this.checked ? 0 : 1] +
						'</span><span class="tzCBPart"></span>'
            });
            // Inserting the new checkbox, and hiding the original:
            checkBox.insertAfter(originalCheckBox.hide());
            checkBox.toggleClass('tzCheckBox');

            var click = function () {
                checkBox.toggleClass('checked');
                var isChecked = checkBox.hasClass('checked');
                // Synchronizing the original checkbox:				
                if (isChecked)
                    originalCheckBox.attr('checked', 'checked');
                else
                    originalCheckBox.removeAttr("checked")
                checkBox.find('.tzCBContent').html(labels[isChecked ? 0 : 1]);
                if (typeof options.selectCheckboxCallback == "function") { //判断当前调用页面是否有回调方法
                    options.selectCheckboxCallback(originalCheckBox);
                }
            }


            if (typeof originalCheckBox.attr("checked") != 'undefined') {
                if (originalCheckBox.attr("checked"))
                    click();
            }
            checkBox.click(function () {
                click()
            });

            // Listening for changes on the original and affecting the new one:
            originalCheckBox.bind('change', function () {
                checkBox.click();
            });
        });
    };
})(jQuery);
