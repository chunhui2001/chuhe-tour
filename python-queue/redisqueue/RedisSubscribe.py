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



