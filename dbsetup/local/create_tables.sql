CREATE TABLE employees (
       id integer PRIMARY KEY,
       name text
);

CREATE TABLE events (
       id serial PRIMARY KEY,
       type text,
       time timestamp,
       employee_id integer REFERENCES employees (id)
);