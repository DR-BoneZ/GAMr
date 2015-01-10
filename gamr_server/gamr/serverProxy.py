from flask import Flask, request
import psycopg2, json

app = Flask(__name__)

#def adduser(username, password, description, games, platforms, genres, seriousness, reputation, miscQuals):
	

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
		return json.dumps(ret);

#print(getUserInfo("BoneZ"))

if __name__ == '__main__':
	app.run(host="0.0.0.0")
