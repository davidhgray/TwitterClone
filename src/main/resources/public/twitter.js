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

// script for creating new  user
// AJAX posting x-www-form-urlencoded
// var createNew = document.getElementById('createNew');
// createNew.addEventListener('click', function () {
//     ajaxPost(
//         '/newUser',
//         {
//             userName: document.getElementById('userName').value,
//             password: document.getElementById('password').value,
//             email: document.getElementById('email').value
//         },
//         (responseText) => {
//             document.getElementById('new_msg').innerHTML = responseText;
//         },
//         (status) => {
//             // TODO show user the error
//             console.log('Request failed.  Returned status of ' + status);
//         }
//     );
// });

// script for logging in existing users
// AJAX posting x-www-form-urlencoded
var returnButton = document.getElementById('returnUser');
returnButton.onclick = function () {
    var xhrReturn = new XMLHttpRequest();
    xhrReturn.open('POST', '/returnUser');
    xhrReturn.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhrReturn.onload = function() {
        if (xhrReturn.status === 200) {
            var result = document.getElementById('ret_msg');
            result.innerHTML = xhrReturn.responseText;
            // else {window.location.assign("/loggedInUser.html");}

        } else if (xhrReturn.status !== 200) {
            alert('Request failed.  Returned status of ' + xhrReturn.status);
        }
    };
    var first = document.getElementById('returnUserName').value;
    var second = document.getElementById('returnUserPassword').value;
    var returnBody = 'returnUserName=' + encodeURIComponent(first) + '&returnUserPassword=' + encodeURIComponent(second);
    console.log(returnBody);
    xhrReturn.send(returnBody);
};
// submit new tweet
var newTweetButton = document.getElementById('newTweetSubmit');
newTweetButton.onclick = function () {
    var xhrNewTweet = new XMLHttpRequest();
    xhrNewTweet.open('POST', '/newTweet'); //change route to /newTweet
    xhrNewTweet.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhrNewTweet.onload = function() {
        if (xhrNewTweet.status === 200) {
            var ntresult = document.getElementById('newTweetSubmit');
            ntresult.innerHTML = xhrNewTweet.responseText;
        } else if (xhrNewTweet.status !== 200) {
            alert('Request failed.  Returned status of ' + xhrNewTweet.status);
        }
    };
    var newTweet = document.getElementById('newTweetContent').value;
    var returnBody = 'newTweetContent=' + encodeURIComponent(newTweet) + '&returnUserName=david';
    console.log(returnBody);
    xhrNewTweet.send(returnBody);
};
