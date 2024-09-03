package crux;

final class Authors {
  // TODO: Add author information.
  static final Author[] all = {new Author("Tarek Hariri", "12288154", "haririt"),
                               new Author("Seongyun Choi","33764098", "seongyuc")};
}


final class Author {
  final String name;
  final String studentId;
  final String uciNetId;

  Author(String name, String studentId, String uciNetId) {
    this.name = name;
    this.studentId = studentId;
    this.uciNetId = uciNetId;
  }
}
