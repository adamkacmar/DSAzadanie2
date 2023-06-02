public class BDDNode {
    private BDDNode leftChild; // left child = 0
    private BDDNode rightChild; // right child = 1
    private String currentExpression;
    private String order;

    BDDNode() {
        this.currentExpression = null;
        this.leftChild = null;
        this.rightChild = null;
    }
    BDDNode(String currentExpression) {
        this.currentExpression = currentExpression;
        this.leftChild = null;
        this.rightChild = null;
    }

    public void setLeftChild(BDDNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(BDDNode rightChild) {
        this.rightChild = rightChild;
    }

    public void setCurrentExpression(String currentExpression) {
        this.currentExpression = currentExpression;
    }

    public void setOrder(String order) { this.order = order; }

    public BDDNode getLeftChild() {
        return leftChild;
    }

    public BDDNode getRightChild() {
        return rightChild;
    }

    public String getCurrentExpression() {
        return currentExpression;
    }

    public String getOrder() { return order; }

}
