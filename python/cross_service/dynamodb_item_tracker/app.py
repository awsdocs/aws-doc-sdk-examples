from datetime import datetime
from flask import Flask, render_template, redirect, url_for, request
import logging
import os
from storage import Storage

app = Flask(__name__)
_STORAGE = None
logger = logging.getLogger(__name__)


def get_storage():
    """Creates or gets the storage object that calls the database."""
    global _STORAGE
    if _STORAGE is None:
        _STORAGE = Storage.from_env()
    return _STORAGE


def add_formatted_date(work_item):
    work_item['formatted_date'] = datetime.fromisoformat(
        work_item['created_date']).strftime('%b %d %Y')


@app.route('/')
@app.route('/items')
def items():
    storage = get_storage()
    logger.info("Getting items for table %s.", storage.table.name)
    work_items = storage.get_items()
    for work_item in work_items:
        add_formatted_date(work_item)
    return render_template(
        'items.html', table=storage.table.name, items=work_items)


@app.route('/item/', methods=['GET', 'POST'])
@app.route('/item/<item_id>', methods=['GET', 'POST'])
def item(item_id=None):
    if request.method == 'GET':
        if item_id is not None:
            work_item = get_storage().get_item(item_id)
            add_formatted_date(work_item)
            return render_template('item.html', **work_item)
        else:
            return render_template('item.html')
    elif request.method == 'POST':
        get_storage().add_or_update_item({
            'item_id': request.form.get('item_id'),
            'name': request.form.get('name'),
            'created_date': request.form.get('created_date'),
            'description': request.form.get('description'),
            'status': request.form.get('status')})
        return redirect(url_for('items'))


@app.route('/item/delete/<item_id>')
def delete_item(item_id):
    get_storage().delete_item(item_id)
    return redirect(url_for('items'))


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        user = request.form['nm']
        return redirect(url_for('hello', name=user))
    else:
        return render_template('login.html')


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    os.environ.setdefault('TABLE_NAME', 'work-items')
    app.run(debug=True)
