<!DOCTYPE html>
<html>
<head>
    <%
        String neonServerUrl = getServletContext().getInitParameter("neon.url");
        String owfServerUrl = getServletContext().getInitParameter("owf.url");
    %>

    <title>Table</title>

    <link rel="stylesheet" type="text/css" href="css/slickgrid/slick.grid.css"/>
    <link rel="stylesheet" type="text/css" href="css/smoothness/jquery-ui-1.8.16.custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/widgetbase.css">
    <link rel="stylesheet" type="text/css" href="css/tablewidget.css">
    <link rel="stylesheet" type="text/css" href="<%=neonServerUrl%>/css/neon.css">

    <script src="<%=owfServerUrl%>/js/owf-widget.js"></script>
    <script src="<%=neonServerUrl%>/js/neon.js"></script>

    <!-- build:js js/tables.js -->
    <script src="js-lib/jquery/jquery-1.7.min.js"></script>
    <script src="js-lib/jquery/jquery.event.drag-2.2.js"></script>
    <script src="js-lib/jquery/jquery-ui-1.8.16.custom.min.js"></script>
    <script src="js-lib/slickgrid/slick.core.js"></script>
    <script src="js-lib/slickgrid/slick.grid.js"></script>
    <script src="js-lib/slickgrid/slick.dataview.js"></script>
    <script src="js-lib/mergesort/merge-sort.js"></script>
    <script src="js-lib/slickgrid/plugins/slick.autotooltips.js"></script>
    <script src="js-lib/bootstrap/bootstrap.min.js"></script>
    <script src="js/toggle.js"></script>
    <script src="javascript/table.js"></script>
    <!-- endbuild -->

    <!-- build:js js/tablewidget.js -->
    <script src="javascript/tablewidget.js"></script>
    <!-- endbuild -->

</head>
<body>
<input type="hidden" id="neon-server" value="<%=neonServerUrl%>"/>

<div id="options-panel" class="options">
    <div class="controls-row">
        <div class="control-group">
            <label class="control-label dropdown-label" for="limit">Limit</label>

            <div id="controls" class="controls">
                <input id="limit" class="dropdown-options" type="number" min="1" value="500">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label dropdown-label" for="sort-field">Sort</label>

            <div id="sort-controls" class="controls-row input-append">

                <select id="sort-field" class="dropdown dropdown-options"></select>

                <div class="btn-group" id="sort-buttons" data-toggle="buttons-radio">
                    <button id="sort-ascending" type="button" data-toggle="button" class="btn btn-small">Ascending
                    </button>
                    <button id="sort-descending" type="button" data-toggle="button" class="btn btn-small">Descending
                    </button>
                </div>

                <input id="sort-direction" type="hidden"/>
            </div>
        </div>
    </div>
</div>

<div id="table"></div>

</body>
</html>

