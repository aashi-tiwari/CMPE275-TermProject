var app=angular.module('examApp',['ngRoute']);

app.controller('mainController',function($scope){

});

app.config(function($routeProvider){
  $routeProvider
    //the timeline display
    .when('/', {
      templateUrl: 'main.html',
      controller: 'authController'
    });
    //the login display
    /*.when('/Login', {
      console.log("Login");
      templateUrl: 'Login.html',
      controller: 'authController'
    })
    //the signup display
    .when('/Register', {
      templateUrl: 'Register.html',
      controller: 'authController'
    });*/
});

app.controller('authController',function($scope)
{
  $scope.user = {username: '',password: ''};
  $scope.error_message = '';

$scope.login = function()
{
  $scope.error_message = 'Login request for ' + $scope.user.username;
};

$scope.register = function()
{
  $scope.error_message = 'Registeration request for ' + $scope.user.username;
};

});
