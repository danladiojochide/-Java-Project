private class TextKeyListener extends KeyAdapter {
    @Override
    public void keyTyped(KeyEvent e) {
        updateStatus();
    }
}