# -*- coding: UTF-8 -*-
import yaml
import os
import sys
from termcolor import colored

class ENV:

    _env = sys.argv[1]
    _pwd = os.path.dirname(os.path.realpath(__file__))
    _config_file = os.path.join(_pwd, 'env.' + _env + '.yaml')
    _config = None

    def __init__(self):

        print colored('Read configuration file: ' + self._config_file, 'green') 

        with open(self._config_file, 'r') as stream:
            try:
                self._config = yaml.load(stream)
            except yaml.YAMLError as exc:
                print(exc)

    def redis(self):
        return self._config['redis_queue']