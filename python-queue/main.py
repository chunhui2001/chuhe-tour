# -*- coding: UTF-8 -*-

import redis

from config import ENV
from redisqueue import Listener

if __name__ == "__main__":
    
    redis_env = ENV().redis()

    r = redis.StrictRedis(
        host=redis_env['redis_host'], port=redis_env['redis_port'], password=redis_env['redis_pass'],
        socket_timeout=None,
        connection_pool=None,
        charset='utf8', errors='strict', unix_socket_path=None)

    client = Listener(r, [redis_env['channel_checkcode_listener']])
    client.start()

# r.publish('chuhe_checkcode_message', 'this is a message will reah the listener2')