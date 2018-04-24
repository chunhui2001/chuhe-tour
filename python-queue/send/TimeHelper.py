# -*- conding: utf-8 -*-

from datetime import datetime
from dateutil.parser import parse
import tzlocal
import pytz
import delorean
import moment


class Helper:

    local_timezone = tzlocal.get_localzone()
    tz_shanghai = pytz.timezone('Asia/Shanghai')
    tz_utc = pytz.timezone('UTC')
    time_format = "%Y-%m-%dT%H:%M:%S.%f %Z%z"

    def __init__(self):
        pass

    def utc_timestamp(self):
        # date_now = datetime.now(tzlocal.get_localzone())
        # time_local = tzlocal.get_localzone().localize(date_now)
        # time_utc = date_now.astimezone(self.tz_utc)
        # time_utc = time_local.astimezone(self.tz_utc)
        #milliseconds = '%03d' % int((now - int(now)) * 1000)
        # return delorean.Delorean(time_local, timezone="Asia/Shanghai").epoch + milliseconds
        # return delorean.Delorean(time_utc, timezone="UTC").epoch

        ep = datetime(1970, 1, 1, 0, 0, 0)
        # return (datetime.utcnow()- ep).total_seconds()

        diff = datetime.utcnow() - ep
        millis = diff.days * 24 * 60 * 60 * 1000
        millis += diff.seconds * 1000
        millis += diff.microseconds / 1000
        return millis

    def printTime(self):

        time_utc = datetime.strptime(
            '2018-04-13T12:26:15.556', "%Y-%m-%dT%H:%M:%S.%f")  # 2018-04-13T12:26:15.556
        time_utc = self.tz_utc.localize(time_utc)
        time_china = time_utc.astimezone(self.tz_shanghai)

        print time_utc.strftime(self.time_format)
        print time_china.strftime(self.time_format)

        print time_utc

        print delorean.Delorean(time_utc, timezone="UTC").epoch
        print delorean.Delorean(time_china, timezone="UTC").epoch

        print datetime.fromtimestamp(delorean.Delorean(
            time_utc, timezone="UTC").epoch, self.tz_utc).strftime(self.time_format)
        print datetime.fromtimestamp(delorean.Delorean(
            time_china, timezone="Asia/Shanghai").epoch, self.local_timezone).strftime(self.time_format)

        print datetime.fromtimestamp(self.utc_timestamp(
        ) / 1000, self.tz_utc).strftime(self.time_format)
