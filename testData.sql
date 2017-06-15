select 'insert into Users values('''|| id || ''',''' || username || ''',''' || email || ''',''' || password|| ''');'  from Users

insert into Users (id,username,email, password)  values('1','david','david@david.com','davidpassword');
insert into Users (id,username,email, password) values('2','elizabeth','elizabeth@eliz.com','elizpassword');
insert into Users (id,username,email, password) values('3','sarah','sarah@sarah.com','sarahpassword');

insert into Following (Follower, Followed) values('1','2');
insert into Following (Follower, Followed) values('1','3');
insert into Following (Follower, Followed) values('2','1');

insert into Tweets (id, content, dt) values('1','its hot here today','2017-06-17');
insert into Tweets (id, content, dt) values('2','snowy in the mountains','2017-06-01');
insert into Tweets (id, content, dt) values('3','hooray for the weekend','2017-04-05');
insert into Tweets (id, content, dt) values('5','hooray for the weekend','2017-04-05');
insert into Tweets (id, content) values('4','another stupid comment');


insert into userTweets (tweetid,userid,dt,originaluserid) values('1', '1', '2017-06-17', null);
insert into userTweets (tweetid,userid,dt,originaluserid) values('2', '2', '2017-06-01', null);
insert into userTweets (tweetid,userid,dt,originaluserid) values('3', '1', '2017-04-05', null);
insert into userTweets (tweetid,userid) values('5', '3');
insert into userTweets (tweetid,userid) values('4', '1');
