from datetime import datetime
from flask import Flask, render_template, redirect, url_for, request, flash
import logging
import os
from storage import Storage, StorageError

app = Flask(__name__)
app.secret_key = 'change-for-production!'
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
def items(error=None):
    storage = get_storage()
    logger.info("Getting items for table %s.", storage.table.name)
    status_filter = request.args.get('status_filter', 'All')

    work_items = []
    try:
        work_items = storage.get_items(status_filter)
    except StorageError as err:
        error = str(err)
    for work_item in work_items:
        add_formatted_date(work_item)
    return render_template(
        'items.html', table=storage.table.name, items=work_items,
        status_filter=status_filter, error=error)


@app.route('/item/', methods=['GET', 'POST'])
@app.route('/item/<item_id>', methods=['GET', 'POST'])
def item(item_id=None):
    if request.method == 'GET':
        work_item = {'item_id': item_id}
        error = None
        if item_id is not None:
            try:
                work_item = get_storage().get_item(item_id)
                add_formatted_date(work_item)
            except StorageError as err:
                error = str(err)
        return render_template('item.html', **work_item, action='get', error=error)
    elif request.method == 'POST':
        work_item = {
            'item_id': request.form.get('item_id'),
            'name': request.form.get('name'),
            'created_date': request.form.get('created_date'),
            'description': request.form.get('description'),
            'status': request.form.get('status')}
        try:
            get_storage().add_or_update_item(work_item)
        except StorageError as err:
            return render_template(
                'item.html', **work_item, action='add or update', error=err)
        else:
            return redirect(url_for('items'))


@app.route('/items/delete/<item_id>')
def delete_item(item_id):
    try:
        get_storage().delete_item(item_id)
    except StorageError as err:
        flash(f"Couldn't delete item '{item_id}'.")
        flash(str(err))
    return redirect(url_for('items'))


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    os.environ.setdefault('TABLE_NAME', 'work-items')
    app.run(debug=True)
