import os
import glob
import time
import random
from sys import argv
import zbar
from bluetooth import *
import smbus
import binascii
import sys
import Adafruit_PN532 as PN532


bus = smbus.SMBus(1)

address = 0x04

enrollfinger = 1
scanfinger = 2
scanQR = 3
scannfc = 4
unlock = 226

os.system('modprobe w1-gpio')	

keys = ['a', 'b', 'c', 'd', 'e', 'f', 'g', '9', '8', '7', '6', '5', '4', '3', '2', '1', '0']

def generateQRString():
	random.shuffle(keys)
	lock_code = ''
	key_length = 10;
	for i in range(0, (key_length-1)):
		rnum = random.randint(0, (len(keys)-1))
		lock_code += keys[rnum]
	return lock_code

def readQR(password):
	results = 'fail'
	scanner = zbar.Processor()
	scanner.parse_config('enable')
	device = '/dev/video0'
	if len(argv) > 1:
		device = argv[1]
	scanner.init(device, False)
#	scanner.visible = True
	scanner.active = True
	try:
		scanner.process_one(15)
		for symbol in scanner.results:
			results = symbol.data
		print 'resuls %s' % results
		if password == results:
			return True
	except (zbar.WindowClosed, zbar.SystemError) as e:
		pass
		return False
	return False

def writeComm(comm):
	bus.write_byte(address, comm)
	return -1
		
def readAns():
	ans = bus.read_byte(address)
	return ans

def masterInstruct(value):
	writeComm(value)
	print "RPI: Instruction - ", value
	if (value == 1):
		time.sleep(10)
	elif (value == 2):
		time.sleep(10)
	else:
		time.sleep(1)

	ans = readAns()
	print "Arduino: Response - ", ans
	print
	return ans

def initNFC():
	CS = 18
	MOSI = 23
	MISO = 24
	SCLK = 25
	
	pn532 = PN532.PN532(cs=CS, sclk=SCLK, mosi=MOSI, miso=MISO)

	pn532.begin()

	ic, ver, rev, support = pn532.get_firmware_version()
	pn532.SAM_configuration()

def enrollNFC():
	initNFC()

	CS = 18
	MOSI = 23
	MISO = 24
	SCLK = 25
	
	pn532 = PN532.PN532(cs=CS, sclk=SCLK, mosi=MOSI, miso=MISO)

	pn532.begin()
	
	counter = 0

	while counter < 10:
		print 'Waiting for Card'
		uid = pn532.read_passive_target()
		if uid != None:
			break
		counter = counter + 1
		time.sleep(1)
	if uid != None:
		f = open('data.txt','w+')
		f.write('0x{0}'.format(binascii.hexlify(uid)))
		f.close()
		return 1
	else:
		return 0	

def scanNFC():
	initNFC()

	CS = 18
	MOSI = 23
	MISO = 24
	SCLK = 25

	pn532 = PN532.PN532(cs=CS, sclk=SCLK, mosi=MOSI, miso=MISO)

	pn532.begin()
	
	counter = 0
	num = 0

	with open('data.txt','r') as f:
		read_data = f.read()
	print(read_data)
	while counter < 10:
		print 'Waiting for MiFare card...'
		uid = pn532.read_passive_target()
		if uid != None:
			num = str('0x{0}'.format(binascii.hexlify(uid)))
			print num
		if read_data == num:
			print 'Found match'
			return 1
		counter = counter + 1
		time.sleep(1)
	return 0

def main():		
	server_sock = BluetoothSocket(RFCOMM)
	server_sock.bind(("", PORT_ANY))
	server_sock.listen(1)

	port = server_sock.getsockname()[1]

	uuid = "0000110E-0000-1000-8000-00805F9B34FB"
	
	unlock_code = 'none'


	advertise_service(server_sock, "LockPi",
					service_id = uuid,
					service_classes = [uuid, SERIAL_PORT_CLASS],
					profiles = [SERIAL_PORT_PROFILE],
					)
	print "Welcome"					
	while True:
		print "Waiting for connection on RFCOMM channel %d" % port
	
		client_sock, client_info = server_sock.accept()
		print "Accepted connection from ", client_info
	
		try:
			data = client_sock.recv(1024)
			if len(data) == 0: 
				break
			print "received [%s]" % data
				
			if data == 'scannfc':
				data = scanNFC()
				if data == 1:
					data = 'Unlocked'
					masterInstruct(unlock)
				else:
					data = 'Please try again' 
			elif data == 'enrollnfc':
				data = enrollNFC()
				if data == 1:
					data = 'Success'
				else:
					data = 'Please try again'
			elif data == 'scanfinger':
				data = masterInstruct(scanfinger)
				if data != 5:
					data = 'Authentication Failed. Please try again.'
				else:
					data = 'Unlocked'
			elif data == 'enrollfinger':
				data = masterInstruct(enrollfinger)
				if data == 10:
					data = 'Success'
				else:
					data = 'Finger Enrollment failed. Please try again.'
			elif data == 'makeqr':
				unlock_code = generateQRString()
				data = unlock_code
			elif data == 'scanqr':
				if unlock_code != 'none':
					check = readQR(unlock_code)
					if check:
						data = 'Unlocked'
						masterInstruct(unlock)
					else:
						data = 'Still locked. Please try again.'
				else:
					data = 'Please use send QR first'
			elif data == 'exit':
				print "disconnected"
				client_sock.close()
				server_sock.close()
				print "all done"
				break
			else:
				data = 'WTF!'
			client_sock.send(data)
			print "sending [%s]" % data
			
		except IOError:
			pass
		
		except KeyboardInterrupt:
			print "disconnected"
			client_sock.close()
			server_sock.close()
			print "all done"
			break

main()
