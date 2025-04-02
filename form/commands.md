docker exec -it postgresdb bash
psql -h formgreendotsdb -d greendotsdb -U testuser 
password: testpassword
SELECT * FROM usuarios;
