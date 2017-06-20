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
var createNew = document.getElementById('createNew');
createNew.addEventListener('click', function () {
    ajaxPost(
        '/newUser',
        {
            userName: document.getElementById('userName').value,
            password: document.getElementById('password').value,
            email: document.getElementById('email').value
        },
        (responseText) => {
            document.getElementById('new_msg').innerHTML = responseText;
        },
        (status) => {
            // TODO show user the error
            console.log('Request failed.  Returned status of ' + status);
        }
    );
});
