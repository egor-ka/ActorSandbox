package actor_tests

object FileSystem {
  def saveBytesToFile(storedMessage: StoredMessage): Boolean = {
    // saving message
    Thread.sleep(1000)
    true
  }
}
