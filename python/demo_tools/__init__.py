def demo_func(func):
    def wrapper(*args, **kwargs):
        print('-'*88)
        func(*args, **kwargs)
        print('-'*88)
    return wrapper
