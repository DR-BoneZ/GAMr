from flask import Flask, request
import psycopg2, json, re

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

def orderByMiscQuals(arr):
	return arr

@app.route("/")
def root():
	return '{"error":"404", "description":"Not Found: The specified user does not exist"}'

@app.route("/user/<user>", methods=['GET', 'POST'])
def getUserInfo(user):
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	if request.method == "GET":
		cur.execute("SELECT * FROM users WHERE username = '" + user + "';")
		ret = cur.fetchone()
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

@app.route("/user/add/<user>", methods=['POST'])
def newUser(user):
	if re.match('^[\w-]+$', user) is None:
		return '{"error":"403", "description":"Forbidden: Please use only alphanumeric characters and dashes."}'
	print getGetUserInfo(user)
	if ("error" in json.loads(getGetUserInfo(user)) and json.loads(getGetUserInfo(user))["error"] == "404"):
		adduser(user, request.form['password'], request.form['description'], request.form['games'], request.form['platforms'], request.form['genres'], request.form['seriousness'], request.form['reputation'], request.form['miscQuals'])
		return getGetUserInfo(user);
	else:
		return '{"error":"420", "description":"Method Failure: The specified username already exists."}'

@app.route("/user/edit/<user>", methods=['PUT'])
def editUser(user):
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + user + "';")
	ret = cur.fetchone()
	if (ret != None and ret[1] == request.form['password']):
		cur.execute("UPDATE users SET " + json.dumps(request.form).replace('{"', '').replace('": "', ' = "').replace('", "', '", ').replace('"}', '"').replace('"', "'") + " WHERE username = '" + user + "';")
		conn.commit()
	return getGetUserInfo(user)

@app.route("/user/del/<user>", methods=['POST'])
def delUser(user):
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + user + "';")
	ret = cur.fetchone()
	if (ret != None and ret[1] == request.form['password']):
		cur.execute("DELETE FROM users WHERE username = '" + user + "';")
		conn.commit()
	return getGetUserInfo(user)

@app.route("/post", methods=['GET', 'POST'])
def search():
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM posts WHERE PLTorTLP = '" + request.form['PLTorTLP'] + "'" + (", platform = '" + request.form['platform'] + "'" if request.form['platform'] else "") + (", game = '" + request.form['game'] + "'" if request.form['game'] else "") + (", genre = '" + request.form['genre'] + "'" if request.form['genre'] else "") + (", username = '" + request.form['username'] + "'" if request.form['username'] else "") + " ORDER BY abs(seriousness - " + request.form['seriousness'] + ") ASC, reputation DESC;")
	ret = cur.fetchall()
	ret = orderByMiscQuals(ret)
	return json.dumps(ret)

@app.route("/post/add", methods=['POST'])
def addPost():
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + request.form['username'] + "';")
        ret = cur.fetchone()
        if (ret != None and ret[1] == request.form['password']):
		cur.execute("INSERT INTO posts(id, username, description, game, platform, genre, seriousness, reputation, PLTorTLP, miscQuals) VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", (request.form['username'] + "" + time.gmtime(0), request.form['username'], request.form['description'], request.form['game'], request.form['platform'], request.form['genre'], request.form['seriousness'], request.form['reputation'], request.form['PLTorTLP'], request.form['miscQuals']))
		conn.commit()
	return request.form['username'] + "" + time.gmtime(0)

@app.route("/post/my", methods=['POST'])
def getMyPosts():
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + request.form['username'] + "';")
	ret = cur.fetchone()
	if (ret != None and ret[1] == request.form['password']):
		cur.execute("SELECT * FROM posts WHERE username = '" + request.form['username'] + "';")
		ret = json.dumps(cur.fetchall())
	else:
		ret = '{"error":"404", "description":"Not Found: The specified username and password combination are invalid."}'
	return ret

@app.route("/post/del", methods=['POST'])
def delPost():
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + request.form['username'] + "';")
	ret = cur.fetchone()
	if (ret != None and ret[1] == request.form['password']):
		cur.execute("DELETE FROM posts WHERE id = '" + request.form['id'] + "';")
		return getMyPosts()
	else:
		return '{"error":"404", "description":"Not Found: The specified username and password combination are invalid."}'

#print(getUserInfo("BoneZ"))

if __name__ == '__main__':
	app.run(host="0.0.0.0", debug=True)
