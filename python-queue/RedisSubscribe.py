import redis
import threading
import json

from notify import Notify


class Listener(threading.Thread):

    def __init__(self, r, channels):
        threading.Thread.__init__(self)
        self.redis = r
        self.pubsub = self.redis.pubsub()
        self.pubsub.subscribe(channels)

    def work(self, item):
        if item['channel'] == 'chuhe_checkcode_message':
            Notify(item['data']).notify()

    def run(self):
        for item in self.pubsub.listen():
            if item['data'] == "KILL":
                self.pubsub.unsubscribe()
                print self, "unsubscribe and finished"
                break
            else:
                self.work(item)


if __name__ == "__main__":

    r = redis.StrictRedis(
        host='192.168.189.175', port=6379, password='Cc',
        socket_timeout=None,
        connection_pool=None,
        charset='utf8', errors='strict', unix_socket_path=None)

    client = Listener(r, ['chuhe_checkcode_message'])
    client.start()

# r.publish('chuhe_checkcode_message', 'this is a message will reah the listener2')
