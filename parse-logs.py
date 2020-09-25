import json

from sys import argv
from datetime import datetime

def help():
	print('today - print all today\'s logs')
	print('websocket - print all websocket logs')
	print('day.month.year - print all logs for a specific date, e.g 3.6.2020')

def load_logs(path_to_file):
	return json.loads(open(path_to_file).read() + ']')


def print_log_entry(entry):
	message = json.loads(entry['message'])
	is_websocket = 'event' in message
	
	print('[Date]', datetime.utcfromtimestamp(entry['instant']['epochSecond']))
	print('[Log Level]', (entry['level'] if 'level' in entry else None))
	

	if is_websocket:
		print('[Event]', message['event'])
		print('[Authorization Header]', (message['auth-header'] if 'auth-header' in message else None))
		print('[Hub ID]', (message['hub-id'] if 'hub-id' in message else None))
		print('[Message]', (message['message'] if 'message' in message else None))
		print('[Error]', (message['error'] if 'error' in message else None))
		print('[Reason]', (message['reason'] if 'reason' in message else None))
	elif 'exception' in message:
		print('Exception', message['exception'])
	else:
		print('[HTTP Status Code]', (message['http-status-code'] if 'http-status-code' in message else None))
		print('[Execution Time Ms]', (message['execution-time-ms'] if 'execution-time-ms' in message else None))
		print('[Method]', (message['method'] if 'method' in message else None))
		print('[Content Type]', (message['content-type'] if 'content-type' in message else None))
		print('[Content Length]', (message['content-length'] if 'content-length' in message else None))
		print('[Full URL]', (message['full-url'] if 'full-url' in message else None))
		print('[Request Body]', (message['body'] if 'body' in message else None))


def print_logs(logs):
	for entry in logs:
		print_log_entry(entry)
		print()


def today(logs):
	date_today = datetime.today()
	today_logs = []
	
	for entry in logs:
		log_date = datetime.utcfromtimestamp(entry['instant']['epochSecond'])
		
		if (log_date.year == date_today.year) and (log_date.month == date_today.month) and (log_date.day == date_today.day):
			today_logs.append(entry)

	return today_logs


def specific_date(logs, day, month, year):
	result = []
	
	for entry in logs:
		log_date = datetime.utcfromtimestamp(entry['instant']['epochSecond'])
		print(log_date.year, log_date.month, log_date.day)
		
		if (year == str(log_date.year)) and (month == str(log_date.month)) and (day == str(log_date.day)):
			result.append(entry)

	return result


def websocket(logs):
	websocket_logs = []

	for entry in logs:
		if 'event' in entry['message']:
			websocket_logs.append(entry)

	return websocket_logs


def parse_params(params, logs):
	if params == []:
		help()
		return
	
	for param in params:
		if param == 'today':
			logs = today(logs)
		elif param == 'websocket':
			logs = websocket(logs)
		elif '.' in param:
			user_date = param.split('.')

			logs = specific_date(logs, user_date[0], user_date[1], user_date[2])

	print_logs(logs)


def main():
	logs = load_logs('application.log')
	params = argv[1:]
	
	parse_params(params, logs)


if __name__ == '__main__':
	main()
