# OSGi Example Project

### Interesting links

[Felix FAQ](https://felix.apache.org/documentation/faqs/apache-felix-bundle-plugin-faq.html)

The only classes that will appear in the bundle are the ones you ask it to include using
`Export-Package`, `Private-Package`, `Include-Resource`, and `Embed-Dependency` -
so just because a file exists under `target/classes` does NOT mean it will end up in the bundle.
This is because this is the way the underlying BND tool works (it is primarily pull-based).

[Annotation Driven Development](https://virtual.osgiusers.org/2018/10/pure-annotation-driven-dev)
