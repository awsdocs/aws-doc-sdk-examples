from datetime import datetime
from flask import Flask, render_template, redirect, url_for, request, flash
import logging
from storage import Storage, StorageError

logger = logging.getLogger(__name__)


def create_app(test_config=None):
    app = Flask(__name__)
    app.config.from_mapping(
        SECRET_KEY='change-for-production!',
        TABLE_NAME='work-items')
    if test_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(test_config)

    def add_formatted_date(work_item):
        work_item['formatted_date'] = datetime.fromisoformat(
            work_item['created_date']).strftime('%b %d %Y')

    @app.route('/')
    @app.route('/items')
    def items(error=None):
        storage = Storage.from_context()
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
        storage = Storage.from_context()
        if request.method == 'GET':
            work_item = {'item_id': item_id}
            error = None
            if item_id is not None:
                try:
                    work_item = storage.get_item(item_id)
                    add_formatted_date(work_item)
                except StorageError as err:
                    error = str(err)
            return render_template('item.html', **work_item, action='get', error=error)
        elif request.method == 'POST':
            work_item = {
                'item_id': item_id,
                'name': request.form.get('name'),
                'description': request.form.get('description'),
                'status': request.form.get('status')}
            try:
                storage.add_or_update_item(work_item)
            except StorageError as err:
                return render_template(
                    'item.html', **work_item, action='add or update', error=err)
            else:
                return redirect(url_for('items'))

    @app.route('/items/delete/<item_id>')
    def delete_item(item_id):
        storage = Storage.from_context()
        try:
            storage.delete_item(item_id)
        except StorageError as err:
            flash(f"Couldn't delete item '{item_id}'.")
            flash(str(err))
        return redirect(url_for('items'))

    return app


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    create_app().run(debug=True)
