select 'insert into Users values('''|| id || ''',''' || username || ''',''' || email || ''',''' || password|| ''');'  from Users

insert into Users values('1','david','david@david.com','davidpassword');
insert into Users values('2','elizabeth','elizabeth@eliz.com','elizpassword');
insert into Users values('3','sarah','sarah@sarah.com','sarahpassword');

insert into Following values('1','2');
insert into Following values('1','3');
insert into Following values('2','1');

insert into Tweets values('1','it's hot here today','2017-06-17');
insert into Tweets values('2','snowy in the mountains','2017-06-01');
insert into Tweets values('3','hooray for the weekend','2017-04-05');
insert into Tweets values('5','hooray for the weekend','2017-04-05');

--tweetid,userid, datetime,originaluserid
insert into userTweets values('1', '1', '2017-06-17', null);
insert into userTweets values('2', '2', '2017-06-01', null);
insert into userTweets values('3', '1', '2017-04-05', null);
insert into userTweets values('5', '3', '2017-04-05', null);
