from flask import Flask, request
import psycopg2, json, re, time

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

def matchedQuals(queryJSON, resultJSON, game):
	matched = 0
	query = json.loads(queryJSON)[game]
	result = json.loads(resultJSON)[game]
	for key in query.keys():
		if query[key].startswith("Rng("):
			if int(query[key].replace('Rng(', '').split('-')[0]) <= result[key] <= int(query[key].replace('Rng(', '').split('-')[1]):
				matched += 1
		else:
			if query[key] == result[key]:
				matched += 1
	return matched

def orderByMiscQuals(arr, miscQuals, game):
	newArr = [];
	for post in arr:
		x = matchedQuals(miscQuals, post['miscQuals'], game)
		for i in range(0, len(newArr)):
			if newArr[i]:
				if x > matchedQuals(miscQuals, newArr[i]['miscQuals'], game):
					newArr.insert(i, post)
					break
			else:
				newArr[i] = post
	return newArr

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
			#check for reply to post
			replies = {};
			posts = json.loads(getMyPosts())
			for post in posts:
				replies[post['id']] = post['replies']
			replies = json.dumps(replies)
			retjson = '{ "username":"' + ret[0] + '", "description":"' + ret[2] + '", "games":' + ret[3] + ', "platforms":' + ret[4] + ', "genres":' + ret[5] + ', "seriousness":' + str(ret[6]) + ', "reputation":' + str(ret[7]) + ', "miscQuals":' + ret[8] + ', "replies":' + replies + ' }'
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

@app.route("/post", methods=['POST'])
def search():
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	form = request.form
	cur.execute("SELECT * FROM posts WHERE PLTorTLP = '" + form['PLTorTLP'] + "'" + (", platform = '" + form['platform'] + "'" if 'platform' in form else "") + (", game = '" + form['game'] + "'" if 'game' in form else "") + (", genre = '" + form['genre'] + "'" if 'genre' in form else "") + (", username = '" + form['username'] + "'" if 'username' in form else "") + " ORDER BY " + ("abs(seriousness - " + form['seriousness'] + ")" if 'seriousness' in form else "") + " ASC, reputation DESC;")
	#cur.execute("SELECT * FROM posts WHERE PLTorTLP = '" + request.form['PLTorTLP'] + "'" + (", platform = '" + request.form['platform'] + "'" if request.form['platform'] else "") + (", game = '" + request.form['game'] + "'" if request.form['game'] else "") + (", genre = '" + request.form['genre'] + "'" if request.form['genre'] else "") + (", username = '" + request.form['username'] + "'" if request.form['username'] else "") + " ORDER BY abs(seriousness - " + request.form['seriousness'] + ") ASC, reputation DESC;")
	ret = cur.fetchall()
	if 'game' in form and 'miscQuals' in form:
		ret = orderByMiscQuals(ret, request.form['miscQuals'], request.form['game'])
	return json.dumps(ret)

@app.route("/post/add", methods=['POST'])
def addPost():
	conn = psycopg2.connect("dbname=gamr user=gamr")
	cur = conn.cursor()
	cur.execute("SELECT * FROM users WHERE username = '" + request.form['username'] + "';")
        ret = cur.fetchone()
	postid = request.form['username'] + "" + str(time.time())
        if (ret != None and ret[1] == request.form['password']):
		cur.execute("INSERT INTO posts(id, username, description, game, platform, genre, seriousness, reputation, PLTorTLP, miscQuals, replies) VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", (postid, request.form['username'], request.form['description'], request.form['game'], request.form['platform'], request.form['genre'], request.form['seriousness'], request.form['reputation'], request.form['PLTorTLP'], request.form['miscQuals'], '[]'))
		conn.commit()
	return postid

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

@app.route("/miscquals/<game_title>", methods=['GET'])
def getTitle(game_title):
	obj = open('titles.json', 'r')
	decoded_title= json.load(obj)
	final_title= json.dumps(decoded_title[game_title])
	return final_title

#print(getUserInfo("BoneZ"))

if __name__ == '__main__':
	app.run(host="0.0.0.0", debug=True)
