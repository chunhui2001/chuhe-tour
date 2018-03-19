

function SelectTable(itemListSelector, allCbxHandlerId, pIdListEleId, delEleId) {

    this.itemListSelector = itemListSelector;
    this.allCbxHandlerId = allCbxHandlerId;
    this.pIdListEleId = pIdListEleId;
    this.delEleId = delEleId;

}


SelectTable.prototype = {
    init: function () {

        var $this = this;

        setBtnDelEnabled($this);

        $($this.itemListSelector + " :checkbox").on("click", function () {

            setBtnDelEnabled($this);
            setProductIds($this, $(this).is(":checked"), $(this).val());

        });

        $("table " + $this.allCbxHandlerId).on("click", function () {
            if ($(this).is(":checked")) {
                $($this.itemListSelector + " :checkbox:not(:checked)").each(function () {
                    $(this).click();
                });
            } else {
                $($this.itemListSelector + " :checkbox:checked").each(function () {
                    $(this).click();
                });
            }
        });

    }
}


function setBtnDelEnabled(selectTable) {
    if ($(selectTable.itemListSelector + " :checkbox:checked").length > 0) {
        $(selectTable.delEleId).attr("disabled", false);
    } else {
        $(selectTable.delEleId).attr("disabled", true);
    }
}

function setProductIds(selectTable, ischeck, productid) {

    if (ischeck) {

        if ($(selectTable.pIdListEleId).val().length == 0) {
            $(selectTable.pIdListEleId).val(productid);
        }


        if ($(selectTable.pIdListEleId).val().length > 0 && !isSelected(selectTable, productid)) {
            $(selectTable.pIdListEleId).val($(selectTable.pIdListEleId).val() + "," + productid);
        }

        return;
    }

    removeProductId(selectTable, productid);

}

function isSelected(selectTable, productid) {
    return ("," + $(selectTable.pIdListEleId).val() + ",").indexOf(',' + productid + ',') != -1;
}

function removeProductId(selectTable, productid) {

    if (!isSelected (selectTable, productid) || $(selectTable.pIdListEleId).val().length == 0) {
        return;
    }

    var newValue = ("," + $(selectTable.pIdListEleId).val() + ",").replace("," + productid + ",", "," );

    $(selectTable.pIdListEleId).val(newValue.substr(1, newValue.length - 2));

}