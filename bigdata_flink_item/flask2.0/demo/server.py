from flask import Flask,render_template
app = Flask(__name__)  # 创建Flask类的一个实例
@app.route("/index",methods=['GET'])
def rount():
    return render_template('index.html')
if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)


