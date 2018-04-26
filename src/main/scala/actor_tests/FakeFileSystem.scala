package actor_tests

object FakeFileSystem {
  def saveBytesToFile(storedMessage: StoredMessage): Boolean = {
    // saving message
    Thread.sleep(1000)
    true
  }
}
