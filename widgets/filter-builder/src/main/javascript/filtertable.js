var neon = neon || {};
neon.filter = (function () {

    var messageHandler = {
        publishMessage: function () {
        }
    };

    if (typeof (OWF) !== "undefined") {
        OWF.ready(function () {
            messageHandler = new neon.eventing.MessageHandler();
        });
    }

    var filterKey;
    var columnOptions;

    var operatorOptions = ["=", "!=", ">", "<", ">=", "<="];

    var FilterRow = function (columnValue, operatorValue, value) {
        this.columnOptions = columnOptions;
        this.columnValue = columnValue;
        this.operatorOptions = operatorOptions;
        this.operatorValue = operatorValue;
        this.value = value;
        this.submittable = false;
    };

    var filterState = {
        data: []
    };

    var addFilter = function (id) {
        var updatingExisting = filterState.data[id].submittable;
        updateDataFromForm(id);
        var filter = buildFilterFromData();

        neon.query.replaceFilter(filterKey, filter, function () {
            if (!updatingExisting) {
                filterState.data.push(new FilterRow());
            }
            messageHandler.publishMessage(neon.eventing.Channels.FILTERS_CHANGED, {});
            redrawTemplateFromData();
        });
    };

    var removeFilter = function (id) {
        filterState.data.splice(id, 1);
        var filter = buildFilterFromData();

        neon.query.replaceFilter(filterKey, filter, function () {
            messageHandler.publishMessage(neon.eventing.Channels.FILTERS_CHANGED, {});
            redrawTemplateFromData();
        });
    };

    function buildCompoundWhereClause(data) {
        var whereClause;
        var clauses = [];
        var selected = $("input[type='radio'][name='boolean']:checked").val();

        $.each(data, function (index, filterData) {
            var clause = neon.query.where(filterData.columnValue, filterData.operatorValue, filterData.value);
            clauses.push(clause);
        });

        if (selected === "AND") {
            whereClause = neon.query.and.apply(this, clauses);
        }
        else {
            whereClause = neon.query.or.apply(this, clauses);
        }
        return whereClause;
    }

    function buildFilterFromData() {
        var dataset = neon.wizard.dataset();
        var baseFilter = new neon.query.Filter().selectFrom(dataset.database, dataset.table);

        var data = getSubmittableData();

        var whereClause;
        if (data.length === 0) {
            return baseFilter;
        }
        if (data.length === 1) {
            var filterData = data[0];
            whereClause = neon.query.where(filterData.columnValue, filterData.operatorValue, filterData.value);
        }
        else {
            whereClause = buildCompoundWhereClause.call(this, data);
        }
        return baseFilter.where(whereClause);
    }

    var getSubmittableData = function () {
        var data = [];
        $.each(filterState.data, function (index, value) {
            if (value.submittable) {
                data.push(value);
            }
        });

        return data;
    };

    var updateDataFromForm = function (id) {
        var filterData = filterState.data[id];
        filterData.columnValue = $('#column-select-' + id + ' option:selected').val();
        filterData.operatorValue = $('#operator-select-' + id + ' option:selected').val();
        filterData.value = $('#value-input-' + id).val();
        filterData.submittable = true;

        if ($.isNumeric(filterData.value)) {
            filterData.value = parseFloat(filterData.value);
        }
        if (filterData.value === "null" || filterData.value === "") {
            filterData.value = null;
        }
        if (filterData.value === '""') {
            filterData.value = "";
        }
    };

    var redrawTemplateFromData = function () {
        var source = $("#filters").html();
        var template = Handlebars.compile(source);
        var html = template(filterState);
        $('#filter-content').html(html);
    };

    var grid = function (columnNames) {
        columnOptions = columnNames;
        filterState.data = [];
        filterState.data.push(new FilterRow());
        redrawTemplateFromData();
    };

    var setFilterKey = function (key) {
        filterKey = key;
    };

    return {
        addFilter: addFilter,
        removeFilter: removeFilter,
        setFilterKey: setFilterKey,
        grid: grid
    };

})();
