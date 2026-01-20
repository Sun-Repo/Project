// linkedlists/ReverseLinkedList.java
class Node {
    int data; Node next;
    Node(int d){ data=d; next=null; }
}

public class ReverseLinkedList {
    static Node head;

    static Node reverse(Node node){
        Node prev=null, curr=node, next=null;
        while(curr != null){
            next=curr.next;
            curr.next=prev;
            prev=curr;
            curr=next;
        }
        return prev;
    }

    public static void main(String[] args){
        head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head = reverse(head);
        while(head != null){
            System.out.print(head.data + " ");
            head=head.next;
        }
    }
}