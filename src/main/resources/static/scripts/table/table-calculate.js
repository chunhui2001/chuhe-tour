
function CalculateTable(tableBodySelector, pk, priceFieldId, countFieldId, totalMoneyFieldId) {

    this.tableBodySelector = "tbody" + tableBodySelector;
    this.pk = pk;
    this.priceFieldId = priceFieldId;
    this.countFieldId = countFieldId;
    this.totalMoneyFieldId = totalMoneyFieldId;

}

CalculateTable.prototype = {

    init: function () {
        keyUpOrDownInCells(this);
        inputFormatAndCheck(this);
    },

    calculateOrderItemTotalMoney: function (row) {
        calculateOrderItemTotalMoney(this, row);
    },

    removeOrderRow: function (sender) {
        removeOrderRow(this, sender);
    },

    setRowNumber:function (row, number) {
        setRowNumber(row, number);
    },
    resortOrderRowNumber: function () {
        resortOrderRowNumber(this);
    },
    isOnlyOneRow:function () {
        return isOnlyOneRow(this);
    },
    clearOrderRow:function (row, rownumber) {
        clearOrderRow(this, row, rownumber);
    },
    fillOutOrderDetailItem: function (row, object) {

        var $this = this;
        Object.keys(object).forEach(function (key) {
            $(row).find("input:text[id='" + key + "']").val(object[key]);
        });

        $(row).attr($this.pk, object[$this.pk]);
        calculateTable.calculateOrderItemTotalMoney(row);
    },

    enterOrTabToSearch: function (url, done) {
        enterOrTabToSearchfunction(this, url, done);
    },
    getData: function (url, id, done) {
        var url = url + "/" + id + ".json";// "/mans/products/" + pid + ".json";
        fetch(url, {
            method: 'get',
            credentials: "same-origin"
        }).then(function(response) {
            return response.json();
        }).then(function(result) {
            return done(result.error, result);
        }).catch(function(err) {
        });
    },
    removeEmptyOrderDetailItemRow: function (btnRemoveId) {
        removeEmptyOrderDetailItemRow(this, btnRemoveId);
    },
    removeOrderItemRow:function (removeButtonSeletor) {
        $this = this;
        $(document).on("click", removeButtonSeletor, function () {
            removeOrderRow($this, this);
        });
    },
    newOrderRow: function (newRowBtnSelector) {
        var $this = this;
        $(newRowBtnSelector).on("click", function () {
            var rowCount = $(calculateTable.tableBodySelector).find(">tr").length;
            var lastRow = $(calculateTable.tableBodySelector).find(">tr:last");
            var newRow = lastRow.clone();
            $this.clearOrderRow(newRow);
            $this.setRowNumber(newRow, rowCount + 1);
            lastRow.after(newRow);
        });
    },
    getOrderDetailItems: function (modelFields) {

        var $this = this;
        var productList = [];

        $($this.tableBodySelector).find(">tr").each(function () {

            var $tr = this;
            var p = {};

            if (!$($tr).attr($this.pk) || $($tr).attr($this.pk).trim().length == 0) {
                return;
            }

            for (var i=0; i<modelFields.length; i++) {
                p[modelFields[i]] = $($tr).find("input:text[id='" + modelFields[i] + "']").val();

                if ($this.countFieldId == modelFields[i] || $this.priceFieldId == modelFields[i]) {
                    p[modelFields[i]] = parseFloat(p[modelFields[i]]);
                    if (isNaN(p[modelFields[i]])) p[modelFields[i]] = 0.00;
                    p[modelFields[i]] = new Number(p[modelFields[i]]).toFixed(2);
                }


            }

            productList.push(p);

        });

        return productList;

    },

    appendOrderItemsToForm: function (modelFields, formIdSelector, fieldname1, fieldname2) {

        var objectList = this.getOrderDetailItems(modelFields);

        for (var i=0; i<objectList.length; i++) {
            $("<input type='hidden' name='" + fieldname1 + i + "' value='" + JSON.stringify(objectList[i]) + "' />")
                .appendTo($(formIdSelector));
        }

        $("<input type='hidden' name='" + fieldname2 + "' value='" + objectList.length + "' />")
            .appendTo($(formIdSelector));
    }

}



function isOnlyOneRow(calculateTable) {
    return $(calculateTable.tableBodySelector).find(">tr").length == 1;
}

function setRowNumber(row, number) {
    $(row).find("input:text:first").val(number);
}

function removeOrderRow(calculateTable, sender) {
    var currentOrderRow = $(sender).parents("tr")[0];
    if (!isOnlyOneRow(calculateTable)) {
        $(currentOrderRow).remove();
        resortOrderRowNumber(calculateTable);
    } else {
        clearOrderRow(calculateTable, currentOrderRow, 1);
    }
}

function clearOrderRow(calculateTable, row, rownumber) {

    $(row).find("input:text").val("");
    $(row).removeAttr(calculateTable.pk);

    if (rownumber)
        $(row).find("input:text:first").val(rownumber);
}

function resortOrderRowNumber(calculateTable) {
    $(calculateTable.tableBodySelector).find(">tr").each(function (index) {
        setRowNumber(this, index + 1);
    });
}


function calculateOrderItemTotalMoney(calculateTable, row) {
    var count = $(row).find("#" + calculateTable.countFieldId).val();
    var price = $(row).find("#" + calculateTable.priceFieldId).val();
    var money = (isNaN(count) ? 0 : count) * (isNaN(price) ? 0 : price);
    $(row).find("input:text[id='" + calculateTable.totalMoneyFieldId + "']").val(money.toFixed(2));

}

function getOrderDetailItemCount(calculateTable) {
    var $this = calculateTable;
    return $($this.tableBodySelector).find(">tr").length;
}

function keyUpOrDownInCells(calculateTable) {

    var $this = calculateTable;

    // 上下箭头单元格移动
    $(document).on("keydown", $this.tableBodySelector + " input:text", function (e) {

        var code = e.keyCode || e.which;
        if (!(code == '40' || code == '38')) {
            return;
        }

        var isdown = code == '40';

        var currentRow = $(this).parents('tr')[0];
        var currentColumn = $(this).parents('td')[0];

        var rowIndex = $(currentRow).prevAll('tr').length;
        var colIndex = $(currentColumn).prevAll('td').length;

        var targtRowIndex = isdown ? rowIndex + 1 : rowIndex - 1;

        if (targtRowIndex < 0) return;
        if (targtRowIndex > getOrderDetailItemCount($this) - 1) return;

        $($this.tableBodySelector).find(">tr:eq(" + targtRowIndex + ")").find("input:text:eq(" + colIndex + ")").focus();

    });

}

function inputFormatAndCheck(calculateTable) {

    var $this = calculateTable;

    $(document).on("keydown", "#" + $this.priceFieldId + ",#" + $this.countFieldId,
        function (e) {

        var code = e.keyCode || e.which;

        // back . , left, right, up, down, enter, 0-9, copy, paste
        if ((code < 48 || code > 57) && [8, 9,13,16,17,37,39, 67, 86,188,190].indexOf(code) == -1) {
            return false;
        }

    });

    $(document).on("input", "#" + $this.priceFieldId + ",#" + $this.countFieldId,
        function (e) {

        var code = e.keyCode || e.which;

        var currentFloat = parseFloat($(this).val());

        if ($(this).val().trim() != '' && isNaN(currentFloat)) {
            $(this).val("0.00");
            return;
        }

        if (code == 86)
            $(this).val(currentFloat);

        $this.calculateOrderItemTotalMoney($(this).parents('tr')[0]);

    });

}

function enterOrTabToSearchfunction (calculateTable, url, done) {

    var $this = calculateTable;

    $(document).on("keydown", "#" + $this.pk, function (e) {

        var code = e.keyCode || e.which;

        if (!(code == '9' || code == '13')) {
            return;
        }

        var _pk = $(this).val();
        var currentRow = $(this).parents('tr')[0];

        if ($(currentRow).attr($this.pk) == _pk.trim()) {
            return;
        }

        $this.getData(url, _pk, function (error, result) {

            if (error || result.code == 404) {
                return;
            }

            $this.fillOutOrderDetailItem(currentRow, result.data);

        });


    });

}

function removeEmptyOrderDetailItemRow(calculateTable, btnRemoveId) {
    $("#" + btnRemoveId).on("click", function () {
        $(calculateTable.tableBodySelector).find(">tr").each(function (index) {

            if (calculateTable.isOnlyOneRow()) {
                return ;
            }

            var isempty = true;

            $(this).find("input:text:not(:first)").each(function () {
                if ($(this).val().length > 0) {
                    isempty = false;
                    return false;
                }
            });

            if (isempty) {
                $(this).remove();
                calculateTable.resortOrderRowNumber();
            }

        });
    });
}

