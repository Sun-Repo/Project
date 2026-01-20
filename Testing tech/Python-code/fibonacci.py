# recursion/fibonacci.py
def fib(n):
    return n if n <= 1 else fib(n-1) + fib(n-2)

print([fib(i) for i in range(7)])
