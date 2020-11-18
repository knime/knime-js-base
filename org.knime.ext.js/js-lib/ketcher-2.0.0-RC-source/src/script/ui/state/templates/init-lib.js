import { storage } from '../../storage-ext';
import sdf from '../../../chem/sdf';
import molfile from '../../../chem/molfile';
import { appUpdate } from '../options';

export function initLib(lib) {
	return {
		type: 'TMPL_INIT',
		data: { lib }
	};
}

export default function initTmplLib(dispatch, baseUrl, cacheEl) {
    // CHANGED Added baseURL
	prefetchStatic(baseUrl + 'library.sdf').then((text) => {
		const tmpls = sdf.parse(text);
		const prefetch = prefetchRender(tmpls, baseUrl, cacheEl);

		return prefetch.then(cachedFiles => (
			tmpls.map((tmpl) => {
				const pr = prefetchSplit(tmpl);
				if (pr.file)
					tmpl.props.prerender = cachedFiles.indexOf(pr.file) !== -1 ? `#${pr.id}` : '';

				return tmpl;
			})
		));
	}).then((res) => {
		const lib = res.concat(userTmpls());
		dispatch(initLib(lib));
		dispatch(appUpdate({ templates: true }));
	});
}

// CHANGED Added function to load local files without CORS
async function fetchLocal(url) {
	return new Promise(((resolve, reject) => {
		const xhr = new XMLHttpRequest()
		xhr.onload = function () {
			resolve(new Response(xhr.responseText, { status: 200 }));
		};
		xhr.onerror = function () {
			reject(new TypeError('Local request failed'));
		};
		xhr.open('GET', url);
		xhr.send(null);
	}));
}

function userTmpls() {
	const userLib = storage.getItem('ketcher-tmpls');
	if (!Array.isArray(userLib) || userLib.length === 0) return [];

	return userLib
		.map((tmpl) => {
			try {
				if (tmpl.props === '') tmpl.props = {};
				tmpl.props.group = 'User Templates';

				return {
					struct: molfile.parse(tmpl.struct),
					props: tmpl.props
				};
			} catch (ex) {
				return null;
			}
		})
		.filter(tmpl => tmpl !== null);
}

function prefetchStatic(url) {
	return fetchLocal(url, { credentials: 'same-origin' }).then((resp) => {
		if (resp.ok)
			return resp.text();
		throw Error('Could not fetch ' + url);
	});
}

function prefetchSplit(tmpl) {
	const pr = tmpl.props.prerender;
	const res = pr && pr.split('#', 2);

	return {
		file: pr && res[0],
		id: pr && res[1]
	};
}

function prefetchRender(tmpls, baseUrl, cacheEl) {
	const files = tmpls.reduce((res, tmpl) => {
		const file = prefetchSplit(tmpl).file;

		if (file && res.indexOf(file) === -1)
			res.push(file);

		return res;
	}, []);

	const fetch = Promise.all(files.map(fn => (
		prefetchStatic(baseUrl + fn).catch(() => null)
	)));

	return fetch.then((svgs) => {
		svgs.forEach((svgContent) => {
			if (svgContent)
				cacheEl.innerHTML += svgContent;
		});

		return files.filter((file, i) => (
			!!svgs[i]
		));
	});
}
