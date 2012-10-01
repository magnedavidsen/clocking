CREATE TABLE users (
       id serial PRIMARY KEY,
       username text,
       email text,
       password text
);

CREATE TABLE lists (
       id serial PRIMARY KEY,
       name text
);

CREATE TABLE items (
       id serial PRIMARY KEY,
       name text,
       quantity integer,
       unit text,
       checked boolean,
       list_id integer REFERENCES lists (id) ON DELETE CASCADE
);

CREATE TABLE users_lists (
       user_id integer REFERENCES users (id) ON DELETE CASCADE,  
       list_id integer REFERENCES lists (id) ON DELETE CASCADE,
       PRIMARY KEY (user_id, list_id)       
);
