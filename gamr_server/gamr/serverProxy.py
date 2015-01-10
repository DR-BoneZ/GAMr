from flask import Flask, request
import psycopg2, json

app = Flask(__name__)

def adduser(username, password, description, games, platforms, genres, seriousness, reputation, miscQuals):
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("INSERT INTO users (username, password, description, games, platforms, genres, seriousness, reputation, miscQuals) VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s)", (username, password, description, games, platforms, genres, seriousness, reputation, miscQuals))
	conn.commit()
	#print str(cur.fetchone())

def getGetUserInfo(user):
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + user + "';");
	ret = cur.fetchone();
	if (ret != None):
		retjson = '{ "username":"' + ret[0] + '", "description":"' + ret[2] + '", "games":' + ret[3] + ', "platforms":' + ret[4] + ', "genres":' + ret[5] + ', "seriousness":' + str(ret[6]) + ', "reputation":' + str(ret[7]) + ', "miscQuals":' + ret[8] + ' }'
	else:
		retjson = '{"error":"404", "description":"Not Found: The specified user does not exist"}'
	return retjson

@app.route("/")
def root():
	return '{"error":"404", "description":"Not Found: The specified user does not exist"}'

@app.route("/user/<user>", methods=['GET', 'POST'])
def getUserInfo(user):
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	if request.method == "GET":
		cur.execute("SELECT * FROM users WHERE username = '" + user + "';");
		ret = cur.fetchone();
		retjson = '{ "username":"' + ret[0] + '", "description":"' + ret[2] + '", "games":' + ret[3] + ', "platforms":' + ret[4] + ', "genres":' + ret[5] + ', "seriousness":' + str(ret[6]) + ', "reputation":' + str(ret[7]) + ', "miscQuals":' + ret[8] + ' }'
		return retjson
	elif request.method == "POST":
		cur.execute("SELECT * FROM users WHERE username = '" + user + "';");
		ret = cur.fetchone();
		if (ret != None and ret[1] == request.form['password']):
			retjson = '{ "username":"' + ret[0] + '", "description":"' + ret[2] + '", "games":' + ret[3] + ', "platforms":' + ret[4] + ', "genres":' + ret[5] + ', "seriousness":' + str(ret[6]) + ', "reputation":' + str(ret[7]) + ', "miscQuals":' + ret[8] + ' }'
			#print retjson
			return retjson
		else:
			#print errortxt
			return '{"error":"404", "description":"Not Found: The specified username and password combination are invalid."}'

@app.route("/add/user/<user>", methods=['POST'])
def newUser(user):
	if (json.loads(getGetUserInfo(user))["error"] == "404"):
		adduser(user, request.form['password'], request.form['description'], request.form['games'], request.form['platforms'], request.form['genres'], request.form['seriousness'], request.form['reputation'], request.form['miscQuals'])
		return getGetUserInfo(user);
	else:
		return '{"error":"420", "description":"Method Failure: The specified username already exists."}'


#print(getUserInfo("BoneZ"))

if __name__ == '__main__':
	app.run(host="0.0.0.0", debug=True)
