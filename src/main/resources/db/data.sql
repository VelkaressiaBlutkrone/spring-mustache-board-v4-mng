insert into board_tb (title, content, writer_id, created_at) values ('title1', 'content1', '1', now());
insert into board_tb (title, content, writer_id, created_at) values ('title2', 'content2', '1', now());
insert into board_tb (title, content, writer_id, created_at) values ('title3', 'content3', '2', now());
insert into board_tb (title, content, writer_id, created_at) values ('title4', 'content4', '2', now());
insert into board_tb (title, content, writer_id, created_at) values ('title5', 'content5', '1', now());
insert into board_tb (title, content, writer_id, created_at) values ('title6', 'content6', '1', now());

-- BCrypt 해시 (비밀번호: 1234) - RULE 1.5.6
insert into user_tb (user_name, user_password, user_email, created_at) values ('ssar', '$2a$10$9Q3oZ5qqnDM14ad/.klzkeXgby5Qx5lMhOkwj0g8xw/zP/HwQ1FqO', 'ssar@email.com', now());
insert into user_tb (user_name, user_password, user_email, created_at) values ('cos', '$2a$10$9Q3oZ5qqnDM14ad/.klzkeXgby5Qx5lMhOkwj0g8xw/zP/HwQ1FqO', 'cos@email.com', now());

insert into reply_tb (user_id, board_id, comment, created_at) values(1, 6, 'comment1', now());
insert into reply_tb (user_id, board_id, comment, created_at) values(1, 6, 'comment2', now());
insert into reply_tb (user_id, board_id, comment, created_at) values(2, 6, 'comment3', now());
insert into reply_tb (user_id, board_id, comment, created_at) values(1, 5, 'comment4', now());
insert into reply_tb (user_id, board_id, comment, created_at) values(2, 5, 'comment5', now());
