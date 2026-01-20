# linkedlists/reverse_linked_list.py
class Node:
    def __init__(self, data):
        self.data = data
        self.next = None

def reverse(head):
    prev = None
    curr = head
    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt
    return prev

# Demo
head = Node(1)
head.next = Node(2)
head.next.next = Node(3)

head = reverse(head)
while head:
    print(head.data, end=" ")
    head = head.next
