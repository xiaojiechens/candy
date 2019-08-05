drop table exchange;
create table exchange(
	tid int auto_increment,
	exchange_date varchar(10),
	account_no varchar(30),
	domain_name varchar(100),
	account_balance double(32,6),
	score_balance double(32,6),
	clear_times int,
	game_times int,
	play_times int,
	bomb_index int,
	primary key(tid)
);

drop table exchange_his;
create table exchange_his(
	tid int auto_increment,
	exchange_date varchar(10),
	account_no varchar(30),
	domain_name varchar(100),
	account_balance double(32,6),
	score_balance double(32,6),
	clear_times int,
	game_times int,
	play_times int,
	bomb_index int,
	primary key(tid)
);

