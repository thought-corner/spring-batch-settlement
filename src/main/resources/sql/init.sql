TRUNCATE TABLE orders;
TRUNCATE TABLE settlement;

DELIMITER
$$

DROP PROCEDURE IF EXISTS fill_dummy_data$$

CREATE PROCEDURE fill_dummy_data()
BEGIN DECLARE
i INT DEFAULT 1;
SET
autocommit = 0;
WHILE
i <= 10000000 DO
        INSERT INTO orders (customer_name, store_name, amount, order_date)
        VALUES (
            CONCAT('고객_', i),
            CASE FLOOR(1 + RAND() * 5)
                WHEN 1 THEN '엽기떡볶이'
                WHEN 2 THEN '교촌치킨'
                WHEN 3 THEN '피자헛'
                WHEN 4 THEN '스타벅스'
                ELSE '김밥천국'
            END,
            FLOOR(1000 + RAND() * 50000),
            CASE
                WHEN RAND() < 0.7 THEN DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY)
                ELSE DATE_SUB(CURRENT_DATE, INTERVAL FLOOR(1 + RAND() * 10) DAY)
            END
        );

        SET
i = i + 1;

        IF
i % 10000 = 0 THEN
            COMMIT;
END IF;
END WHILE;

COMMIT;
SET
autocommit = 1;
END$$

DELIMITER ;

-- 프로시저 실행
CALL fill_dummy_data();


select count(*)
from orders;