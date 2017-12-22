function onLoadFunction()
{
  console.log('In api key');
	gapi.client.setApiKey('AIzaSyApadyDEBA-zlpdvPWgOY-YKhzpxBgPmTU');
	gapi.client.load('plus', 'v1', function() {});
};

 window.fbAsyncInit = function() {
    FB.init({
      appId            : '1415570661887301',
      autoLogAppEvents : true,
      xfbml            : true,
      version          : 'v2.11',
      status           : true
    });

    FB.getLoginStatus(function(response){
      if(response.status === 'connected')
      {

      }
      else if(response.status === 'not_authorized')
      {

      }
      else
      {

      }
      })
  };

  (function(d, s, id){
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement(s); js.id = id;
     js.src = "https://connect.facebook.net/en_US/sdk.js";
     fjs.parentNode.insertBefore(js, fjs);
   }(document, 'script', 'facebook-jssdk'));
