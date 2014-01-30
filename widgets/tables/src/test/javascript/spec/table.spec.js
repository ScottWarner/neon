/*
 * Copyright 2013 Next Century Corporation
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */




describe('table', function () {


    beforeEach(function () {
        setFixtures("<div id='table'></div>");

    });

    it('converts the column names to slick grid format', function () {

        var columnNames = ['c1', 'c2', 'c3'];
        var data = [
            {"c1": "v1", "c2": "v2", "c3": "v3"}
        ];

        var slickgridFormat = [
            { id: 'c1', name: 'c1', field: 'c1'},
            { id: 'c2', name: 'c2', field: 'c2'},
            { id: 'c3', name: 'c3', field: 'c3'}
        ];

        // we only care about the id, name and field properties for this test since they indicate how the
        // column's data is configured
        var table = new tables.Table('#table', {data: data, columns: columnNames});
        var actual = table.columns_.map(function (row) {
            return lodash.pick(row, 'id', 'name', 'field');
        });

        expect(actual).toBeEqualArray(slickgridFormat);

    });

    it('gathers the column names if they are not explicitly specified', function () {

        var data = [
            {"c1": "v1", "c2": "v2"},
            {"c1": "v1", "c2": "v2", "c3": "v3"}
        ];

        var slickgridFormat = [
            { id: 'c1', name: 'c1', field: 'c1'},
            { id: 'c2', name: 'c2', field: 'c2'},
            { id: 'c3', name: 'c3', field: 'c3'}
        ];

        // we only care about the id, name and field properties for this test since they indicate how the
        // column's data is configured
        var table = new tables.Table('#table', {data: data});
        var actual = table.columns_.map(function (row) {
            return lodash.pick(row, 'id', 'name', 'field');
        });

        expect(actual).toBeEqualArray(slickgridFormat);


    });

    it('appends sets up the slickgrid dataview', function() {
        var columnNames = ['c1', 'c2', 'c3'];
        var data = [
            {"c1": "v1", "c2": "v2", "c3": "v3"}
        ];
        var table = new tables.Table('#table', {data: data, columns: columnNames});
        expect(table.dataView_.getItems()).toBeEqualArray(data);

    });

    it('appends an id field to the data if none is specified', function () {
        var data = [
            {"field1": 1, "field2": 2},
            {"field1": 3, "field2": 4},
            {"field1": 5, "field2": 6}
        ];

        var dataWithId = [
            {"field1": 1, "field2": 2},
            {"field1": 3, "field2": 4},
            {"field1": 5, "field2": 6}
        ];
        dataWithId[0][tables.Table.AUTOGENERATED_ID_FIELD_NAME_] = 0;
        dataWithId[1][tables.Table.AUTOGENERATED_ID_FIELD_NAME_] = 1;
        dataWithId[2][tables.Table.AUTOGENERATED_ID_FIELD_NAME_] = 2;

        // creating the table modifies the data
        new tables.Table('#table', { data: data, columns: ['field1', 'field2']});

        // the data should have been modified to append an id field
        expect(data).toBeEqualArray(dataWithId);
    });

    it('does not modify the data if an id field is specified', function () {
        var data = [
            {"myid": 123, "field1": 1, "field2": 2},
            {"myid": 456, "field1": 3, "field2": 4},
            {"myid": 789, "field1": 5, "field2": 6}
        ];

        // because an id field is specified, the expected data should match the original
        var expected = [
            {"myid": 123, "field1": 1, "field2": 2},
            {"myid": 456, "field1": 3, "field2": 4},
            {"myid": 789, "field1": 5, "field2": 6}
        ];

        new tables.Table('#table', { data: data, columns: ['field1', 'field2'], id: "myid"});
        expect(data).toBeEqualArray(expected);

    });

    it('sorts the data ascending', function () {
        setFixtures("<div id='table'></div>");
        var data = [
            { "field1": 3, "field2": 3},
            { "field1": 5, "field2": 7},
            { "field1": 1, "field2": 6}
        ];

        // simulate a sort
        var table = new tables.Table('#table', { data: data, columns: ['field1', 'field2']}).draw();
        clickHeader(table, 'field1');

        // the data should have been sorted
        var sorted = table.dataView_.getItems();
        expect(sorted[0].field1).toEqual(1);
        expect(sorted[1].field1).toEqual(3);
        expect(sorted[2].field1).toEqual(5);

        // click on a different column and the header should be resorted
        clickHeader(table, 'field2');
        sorted = table.dataView_.getItems();
        expect(sorted[0].field2).toEqual(3);
        expect(sorted[1].field2).toEqual(6);
        expect(sorted[2].field2).toEqual(7);


    });

    it('sorts the data descending', function () {
        var data = [
            { "field1": 3, "field2": 3},
            { "field1": 5, "field2": 7},
            { "field1": 1, "field2": 6}
        ];

        // simulate a sort, but click twice to get to descending (first click is ascending)
        var table = new tables.Table('#table', { data: data, columns: ['field1', 'field2']}).draw();
        clickHeader(table, 'field1');
        clickHeader(table, 'field1');

        // the data should have been sorted
        var sorted = table.dataView_.getItems();
        expect(sorted[0].field1).toEqual(5);
        expect(sorted[1].field1).toEqual(3);
        expect(sorted[2].field1).toEqual(1);

        // click on a different column and the header should be resorted (click twice to sort descending)
        clickHeader(table, 'field2');
        clickHeader(table, 'field2');
        sorted = table.dataView_.getItems();
        expect(sorted[0].field2).toEqual(7);
        expect(sorted[1].field2).toEqual(6);
        expect(sorted[2].field2).toEqual(3);


    });

    function clickHeader(table, columnName) {
        var colIndex = table.table_.getColumnIndex(columnName);
        $('.slick-header-columns').children().eq(colIndex).trigger('click');
    }

});
