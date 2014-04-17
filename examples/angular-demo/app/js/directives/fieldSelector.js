'use strict';

/*
 * Copyright 2014 Next Century Corporation
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
var fieldSelector = angular.module('fieldSelectorDirective', []);

fieldSelector.directive('fieldselector', ['ConnectionService', function(connectionService) {
	var link = function($scope, el, attr) {
		var messenger = new neon.eventing.Messenger();
		$scope.database = '';
		$scope.tableName = '';
		$scope.fields = ["test1", "test2"];
		$scope.tmp = "";

		//add select
		//el.append('<select><option>Boo</option></select>');

		var initialize = function() {
			messenger.events({
				activeDatasetChanged: onDatasetChanged
			});
		};

		var onDatasetChanged = function(message) {
			$scope.database = message.database;
			$scope.table = message.table;

			connectionService.getActiveConnection().getFieldNames($scope.table, function(results) {

				$scope.$apply(function() {
					$scope.fields = results;
				});
			});

			if($scope.defaultMapping) {
				$scope.targetVar = connectionService.getFieldMapping($scope.database, $scope.table, $scope.defaultMapping).mapping;
			}
		};

		$scope.onSelectionChange = function() {

		};

		initialize();
	};

	return {
		template: '<label>{{labelText}}</label><select ng-model="targetVar" ng-options="field for field in fields"></select>',
		restrict: 'E',
		scope: {
			targetVar: '=',
			labelText: '=',
			defaultMapping: '='
		},
		link: link
	};
}]);