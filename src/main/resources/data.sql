INSERT INTO restaurant (id, name, latitude, longitude, preparation_time) VALUES (1, 'R1', 12.9372, 77.6185, 20);
INSERT INTO restaurant (id, name, latitude, longitude, preparation_time) VALUES (2, 'R2', 12.9344, 77.6252, 15);


INSERT INTO customer (id, name, latitude, longitude) VALUES (11, 'C1', 12.9351, 77.6245);
INSERT INTO customer ( id, name, latitude, longitude) VALUES (22, 'C2', 12.9367, 77.6200);

INSERT INTO delivery_executive ( id, name, latitude, longitude) VALUES (33, 'D1', 12.9346, 77.6266);

INSERT INTO orders (customer_id, restaurant_id) VALUES
    ((SELECT id FROM customer WHERE name = 'C1'), (SELECT id FROM restaurant WHERE name = 'R1')),
    ((SELECT id FROM customer WHERE name = 'C2'), (SELECT id FROM restaurant WHERE name = 'R2'));
