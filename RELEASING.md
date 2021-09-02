# RELEASING

1. Change the `libVersionName` in top-level build.gradle.
2. Add an entry to CHANGELOG.md to reflect the impending release.
3. Update `implementation 'com.ww:roxie:X.Y.Z'` in README.md where `X.Y.Z` reflects the new version.
4. Merge this release branch into remote master.
5. Check out master from remote: `git checkout master && git pull origin master`
6. Ensure that `signing.keyId`, `signing.password`, `signing.key`, `ossrhUsername`, `ossrhPassword`, and `sonatypeStagingProfileId` for ww-tech is set in local.properties
7. Execute `./gradlew clean build roxie:publishReleasePublicationToSonatypeRepository`
8. Visit https://s01.oss.sonatype.org/ to promote the generated artifacts to maven central.
9. Tag this release: `git tag -a vX.Y.Z -m "Version vX.Y.Z"` (where X.Y.Z is the new version) or provide a short release summary in the message.
10. Push the new tag to origin: `git push origin --tags`