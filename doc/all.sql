create table `user`(
`id` bigint not null ,
`login_name` varchar(50) not null ,
`name` varchar(50),
`password` char(32) not null ,
primary key (`id`),
unique key `login_name_unique` (`login_name`)
)engine=innodb default charset=utf8mb4;