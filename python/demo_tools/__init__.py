def demo_func(func):
    def wrapper(*args, **kwargs):
        print('-'*88)
        result = func(*args, **kwargs)
        print('-'*88)
        return result
    return wrapper
