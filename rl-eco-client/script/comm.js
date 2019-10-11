angular.module('Communication', ['ngWebSocket'])

.factory('MyData', function($websocket) {
  // Open a WebSocket connection
  var dataStream = $websocket('ws://localhost:33313');

  var collection = [];

  dataStream.onMessage(function(message) {
    collection.push(JSON.parse(message.data));
  });

  var methods = {
    collection: collection,
    get: function() {
      dataStream.send(JSON.stringify({ action: 'get' }));
    }
  };

  return methods;
})
.controller('SomeController', function ($scope, MyData) {
  $scope.MyData = MyData;
});
