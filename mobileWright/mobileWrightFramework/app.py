from flask import Flask, render_template, request, redirect, url_for, session
from datetime import datetime
import os

app = Flask(__name__)
app.secret_key = 'your_secret_key'  # Replace with a secure key in production

# Dummy user for login
dummy_user = {'username': 'user', 'password': 'pass'}

@app.route('/', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if username == dummy_user['username'] and password == dummy_user['password']:
            session['user'] = username
            return redirect(url_for('book'))
        else:
            return render_template('login.html', error='Invalid credentials')
    return render_template('login.html')

@app.route('/book', methods=['GET', 'POST'])
def book():
    if 'user' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        date = request.form['date']
        suv_type = request.form['suv_type']
        session['booking'] = {'date': date, 'suv_type': suv_type}
        return redirect(url_for('payment'))
    return render_template('book.html')

@app.route('/payment', methods=['GET', 'POST'])
def payment():
    if 'user' not in session or 'booking' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        card_number = request.form['card_number']
        expiry = request.form['expiry']
        cvv = request.form['cvv']
        session['payment'] = {'card_number': card_number, 'expiry': expiry, 'cvv': cvv}
        return redirect(url_for('report'))
    return render_template('payment.html')

@app.route('/report')
def report():
    if 'user' not in session or 'booking' not in session or 'payment' not in session:
        return redirect(url_for('login'))
    booking = session['booking']
    payment = session['payment']
    report_html = render_template('report.html', user=session['user'], booking=booking, payment=payment)
    # Optionally save report as HTML file
    with open('booking_report.html', 'w', encoding='utf-8') as f:
        f.write(report_html)
    return report_html

@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))

if __name__ == '__main__':
    app.run(debug=True)
