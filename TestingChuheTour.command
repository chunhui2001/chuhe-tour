Docker Command: 
### app.TestingChuheTour.container.volume:
docker create --net TestingChuheTour_network -h app.TestingChuheTour.container.volume  -v /home/ubuntu/workspace/db_chuhetour_testing:/var/lib/mysql_data -v /home/ubuntu/workspace/chuhe-tour:/root/app --name app.TestingChuheTour.container.volume chunhui2001/centos_6_dev:basic 

### app.TestingChuheTour.jdk.web.1:
docker create -P --net TestingChuheTour_network -h app.TestingChuheTour.jdk.web.1 --volumes-from app.TestingChuheTour.container.volume --name app.TestingChuheTour.jdk.web.1  chunhui2001/ubuntu_1610_dev:java8 /root/app/redeploy.sh testing

### app.TestingChuheTour.redis.primary:
docker create -P --net TestingChuheTour_network -h app.TestingChuheTour.redis.primary --volumes-from app.TestingChuheTour.container.volume --name app.TestingChuheTour.redis.primary  chunhui2001/centos_6_dev:redis_instance --requirepass 1miz0dK0x9TyYvba+j7WD2FPwuJo+iBn1iBBuzgaww

### app.TestingChuheTour.mysql.primary:
docker create -P --net TestingChuheTour_network -h app.TestingChuheTour.mysql.primary --volumes-from app.TestingChuheTour.container.volume --name app.TestingChuheTour.mysql.primary -e MYSQL_ROOT_PASSWORD=Cc chunhui2001/debian_8_dev:mysql_5.7 


EOF
