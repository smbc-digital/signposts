import requests

headers = {
    'Accept': 'application/vnd.snap-ci.com.v1+json',
}

print("downloading with requests")

r = requests.get('https://api.snap-ci.com/project/smbc-digital/sonar/branch/master/artifacts/tracking-pipeline/latest/build-jars/1/visualise/target/visualise.jar',
                 headers=headers, auth=('zeshanrasul', 'xre_r6ph9EWWawNqSLk4qeALK8JoEmvdB_KNgNooOas'),
                 allow_redirects=True)

with open("visualise.jar", "wb") as code:
    code.write(r.content)