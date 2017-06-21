function ajaxPost(url, body, success, failure) {
  var xhr = new XMLHttpRequest();
  xhr.open('POST', url);
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhr.onload = function() {
    if (xhr.status === 200) {
      success(xhr.responseText);
    } else if (xhr.status !== 200) {
      failure(xhr.status);
    }
  };
  var s = "";
  for (var k in body) {
    s += k + '=' + encodeURIComponent(body[k]) + '&';
  }
  s = s.substring(0, s.length - 1);
  xhr.send(s);
}


function getTimeline() {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', '/api/timeline');
  xhr.onload = function(evt) {
    if (xhr.status === 200) {
      var response = JSON.parse(xhr.responseText);
      var timelineDiv = document.getElementById('timeline');
      timelineDiv.innerHTML = '';
      for (var i in response) {
        var tweet = response[i];
        var div = document.createElement('div');
        div.setAttribute('class', 'Sizzling');
        var ul = document.createElement('ul');
        var username = document.createElement('li');
        var content = document.createElement('li');
        var tweetDt = document.createElement('li');
        username.innerHTML = tweet.username;
        content.innerHTML = tweet.content;
        tweetDt.innerHTML = tweet.tweetDt;
        ul.appendChild(username);
        ul.appendChild(content);
        ul.appendChild(tweetDt);
        div.appendChild(ul);
        timelineDiv.appendChild(div);
      }
    }
  }
  xhr.send();
}

getTimeline();

setInterval(getTimeline, 10000);


// submit new tweet
var newTweetButton = document.getElementById('newTweetSubmit');
newTweetButton.onclick = function() {
  var xhrNewTweet = new XMLHttpRequest();
  xhrNewTweet.open('POST', '/api/newTweet');
  xhrNewTweet.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhrNewTweet.onload = function() {
    if (xhrNewTweet.status === 200) {
      getTimeline();
    } else if (xhrNewTweet.status !== 200) {
      alert('Request failed.  Returned status of ' + xhrNewTweet.status);
    }
  };
  var newTweet = document.getElementById('newTweetContent').value;
  var returnBody = 'newTweetContent=' + encodeURIComponent(newTweet);
  console.log(returnBody);
  xhrNewTweet.send(returnBody);
};
// see list of popular tweets
var popularButton = document.getElementById('popular');
popularButton.onclick = function(evt) {
  var xhrReturn = new XMLHttpRequest();
  xhrReturn.open('GET', '/popular');
  xhrReturn.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  if (xhrNewTweet.status === 200) {
    getPopular();
  } else if (xhrNewTweet.status !== 200) {
    alert('Request failed.  Returned status of ' + xhrNewTweet.status);
  }
  xhrReturn.send();
};
